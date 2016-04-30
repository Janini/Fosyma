package mas.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentState;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.leap.HashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mas.abstractAgent;
import mas.behaviours.InterblocageBehaviour;
import mas.behaviours.MoveBehaviour;
import mas.behaviours.ReceiveGeneralBehaviour;
import mas.behaviours.SayHelloBehaviour;
import mas.behaviours.UpdateMapBehaviour;
import env.Environment;

public class ExploAgent extends abstractAgent{
	
	private static final long serialVersionUID = -1781844543772918359L;
	
	private List<String> vus; // SALLES DEJA VISITEES OU CONNUES
	private List<String> path;
	private String start;
	private String lastPos = "";
	private HashMap<String,List<String>> alreadySent = new HashMap<String, List<String>>(); //CE QUE J'AI DEJA ENVOYE A L'AGENT i
	public int ESSAISCOMMUNICATION = 3, ESSAISDEPLACEMENT = 3;
	public int nbEssai = 0;
	public int nbEssaiDeplacement = 0;
	protected String rdv = "None"; //POINT DE RDV APRES L'EXPLORATION
	public boolean block = false; //EMPECHE L'AGENT DE SE DEPLACER EN CAS DE COMMUNICATION AVEC UN AUTRE
	public boolean interblocage = false;
	public String wantedToGo = ""; //EN CAS D'INTERBLOCAGE, LA SALLE INACCESSIBLE
	public HashSet obs = new HashSet(); //LISTE DES SALLES OBSERVEES
	public String lastMessage = ""; //PERMET DE TERMINER UNE CONVERSATION
	public HashMap<String,Integer> listTresors = new HashMap<String, Integer>(); //MAPPING SALLE QUI CONTIENT UN TRESOR - VALEUR DU TRESOR 
	
	public static final Object lock = new Object();
	
	protected void setup(){

		super.setup();
		
		final Object[] args = getArguments();
		if(args[0]!=null){

			deployAgent((Environment) args[0]);

		}else{
			System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
			System.exit(-1);
		}
		

		//BEHAVIOURS
		SequentialBehaviour sb = new SequentialBehaviour() {
			private static final long serialVersionUID = 1L;
			public int onEnd() {
			    reset();
			    doWait(500);
			    myAgent.addBehaviour(this);
			    return super.onEnd();
			  }
			};
			sb.addSubBehaviour(new MoveBehaviour(this));
			sb.addSubBehaviour(new UpdateMapBehaviour(this));
			sb.addSubBehaviour(new ReceiveGeneralBehaviour(this));

			addBehaviour(sb);


		this.vus = new ArrayList<String>();
		this.path = new ArrayList<String>();
		
		//ENREGISTREMENT EN TANT QU'EXPLOAGENT
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ExploAgent"); 
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd );
		} catch (FIPAException fe) { fe.printStackTrace(); }
		
		System.out.println("The agent "+getLocalName()+ " is started, capacity = "+getBackPackFreeSpace());

	}
	
	protected void takeDown(){
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) { fe.printStackTrace(); }
		System.out.println(this.getLocalName() + " is terminated");
	}
	
	
	/********************************GETTERS/SETTERS****************************************/
	
	//RENVOIE LA LISTE DES NOEUDS CONNUS DE L'AGENT (VISITES + RECUS PAR D'AUTRES AGENTS)
	public List<String> getVus(){
		return vus;
	}
	
	//RENVOIE LE CHEMIN PARCOURU PAR L'AGENT
	public List<String> getPath(){
		return path;
	}
	
	public String getStart(){
		return start;
	}
	
	//RENVOIE L'ENSEMBLE DES AGENTS DU REPERTOIRE
	public List<AID> getAgents(){
		List<AID> res = new ArrayList<AID>();
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ExploAgent"); 
		dfd.addServices(sd);
		DFAgentDescription[] result = null;
		try {
			result = DFService.search(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		
		if (result != null){
			if (result.length>0){
				for (int i = 0; i < result.length; i++){
					if (!result[i].getName().equals(this.getAID())){
						res.add(result[i].getName());
					}
				}
			}
		}
		return res;
	}

	public String getLastPos(){
		return lastPos;
	}
	
	public void setLastPos(String pos){
		this.lastPos = pos;
	}
	
	public void setStart(String start){
		this.start = start;
	}
		
	public String getRdv(){
		return rdv;
	}
	
	public void setRdv(String rdv){
		if (this.rdv.equals("None") && rdv != "None"){
			this.rdv = rdv;
		}
	}
	
	
	/*******************************TRAITEMENTS SUR DES NOEUDS****************************************/
	
	public void addVus(String pos){
		if (!vus.contains(pos))
			vus.add(pos);
	}
	
	//PARSE LE CONTENU D'UNE LISTE DE NOEUDS, DE LA FORME [noeud1, ..., noeudn] ET AJOUTE A LA MAP
	public void majSend(String agName, String data){
		
		//LISTE VIDE
		if (data.equals("[]"))
			return;
		
		//CE QUE J'AI DEJA ENVOYE A agName
		List<String> update = alreadySent.get(agName);
			
			//ON RECUPERE LE CONTENU DE LA LISTE (-[])
		String[] listvus = null;
		String[] g = data.split("\\[");
		String[] pos = g[1].split("]");
		
			
		try {
			//IL Y A AU MOINS DEUX NOEUDS DANS LA LISTE
			listvus = pos[0].split(", ");
		} catch (Exception e){
				//S'IL N'Y A QU'UN NOEUD (SPLIT AVEC UN REGEX = ", " ECHOUE)
		}
		
		// SI J'AI DEJA CROISE agName
		if (alreadySent.containsKey(agName)){
			for (int i = 0; i < listvus.length; i++){
				if (!alreadySent.get(agName).contains(listvus[i])){
					update.add(listvus[i]);
				}
			}
			
		} else {
			//SI JE NE L'AI JAMAIS VU, AJOUTE UNE NOUVELLE ENTREE			
			update = new ArrayList<String>();
			for (int i = 0; i < listvus.length; i++){
					update.add(listvus[i]);
			}
		}
		
		 // AJOUT DES NOEUDS RECUS
		alreadySent.put(agName, update);
	}
	
	
	/******************************************************************/
	
	//N'ENVOIE QUE LES MESSAGES PAS ENCORE TRANSMIS A SENDER
	public List<String> filtre(List<String> vus, String sender){
		List<String> copyVus = new ArrayList<String>(vus);
		//NOEUDS DEJA ENVOYES A SENDER
		//System.out.println("CE QUE J'AI DEJA ENVOYE A "+sender+ " : "+alreadySent.get(sender));
		if (alreadySent.get(sender) != null)
			copyVus.removeAll(alreadySent.get(sender));
		//System.out.println("CE QUE J'ENVOIE A "+sender+ " : "+ copyVus);
		return copyVus;
		
	}
	


	
	public void echecEnvoi(){
		System.out.println(this.getLocalName()+" : J'essaye de parler (" + nbEssai + ")");
		if (nbEssai == ESSAISCOMMUNICATION){
			System.out.println("ECHEC DE L'ENVOI");
			this.block = false;
			nbEssai = 0;
		} else
			nbEssai++;
	}
	
	
	public void resetEchecs(){
		this.nbEssai = 0;
	}
	
	public void resetInterblocage(){
		this.nbEssaiDeplacement = 0;
	}
	
	public void echecDeplacement(){
		System.out.println(this.getLocalName()+" : J'essaye de bouger (" + nbEssaiDeplacement + ")");
		if (this.nbEssaiDeplacement == ESSAISDEPLACEMENT){
			System.out.println("INTERBLOCAGE");
			this.interblocage = true;
		} else
			this.nbEssaiDeplacement++;
		
	}
	
	
	
	
	
	
	//On maj ce qui a ete transmis e ag (apres reception de ack)
	/*public void majSend(String agName, String vu){
		
		List<String> update = alreadySent.get(agName); // Ce que j'ai deje envoye e agName

		if (alreadySent.containsKey(agName)){ // Si j'ai deja croise agName
			System.out.println("J'ai deja croise "+agName+" !");
			
			 // Je n'envoie que ce qui est nouveau, ced vu
				if (!alreadySent.get(agName).contains(vu)){
					update.add(vu);
					alreadySent.put(agName,update);
				}
			
		} else { // Si je ne l'ai jamais vu, ajoute une nouvelle entree
			update = new ArrayList<String>();
			update.add(vu);
			System.out.println("Je rencontre "+agName+" pour la premiere fois, j'ajoute "+update);
			alreadySent.put(agName, update);
		}
		
		
		Iterator<String> keySetIterator = alreadySent.keySet().iterator();
		while(keySetIterator.hasNext()){
			String cle = keySetIterator.next();
			System.out.println("key: " + cle + " value: " + alreadySent.get(cle));
		}

	}*/

	
}
