package mas.agents;

import jade.core.Agent;
import jade.core.ContainerID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mas.abstractAgent;
import mas.agents.interactions.protocols.deployMe.R1_deployMe;
import mas.agents.interactions.protocols.deployMe.R1_managerAnswer;
import mas.behaviours.DeployedBehaviour;
import mas.behaviours.MoveBehaviour;
import mas.behaviours.ReceiveGeneralBehaviour;
import mas.behaviours.UpdateMapBehaviour;
import env.Environment;

public class ExploAgentBis extends abstractAgent{
	
	private static final long serialVersionUID = -1781844543772918359L;
	
	private List<String> vus;
	private List<String> path;
	private String start;
	private String lastPos = "";
	private HashMap<String,List<String>> alreadySent = new HashMap<String, List<String>>();
	public static int nbEssai = 3;
	public int nbEssaiDeplacement = 3;
	public static List<Agent> repertoire = new ArrayList<Agent>();
	
	public static boolean block = false;
	private String gateKeeperName = "GK";
	
	protected void setup(){

		super.setup();
		
		
		//get the parameters given into the object[]. In the current case, the environment where the agent will evolve
		/*final Object[] args = getArguments();
		if(args[0]!=null){

			deployAgent((Environment) args[0]);

		}else{
			System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
			System.exit(-1);
		}*/
		
		//Container fixe pour deplacer l'agent vers le container Pouet(?)
		ContainerID cID= new ContainerID();
		cID.setName("MyDistantContainer0");
		cID.setPort("8888");
		cID.setAddress("132.227.112.239"); //IP of the host of the targeted container
		this.doMove(cID);// last method to call in the behaviour
		

	
		
		//Add the behaviours
		/*if(!block){
			addBehaviour(new MoveBehaviour(this));
			addBehaviour(new UpdateMapBehaviour(this));
		}
		addBehaviour(new ReceiveGeneralBehaviour(this));*/
		//addBehaviour(new SendAckBehaviour(this));
		addBehaviour(new R1_deployMe(gateKeeperName,this));
		addBehaviour(new R1_managerAnswer(gateKeeperName,this));

		//Creation de la liste de cases
		this.vus = new ArrayList<String>();
		this.path = new ArrayList<String>();
		repertoire.add(this);
		System.out.println("The agent "+getLocalName()+ " is started");

	}
	
	protected void takeDown(){

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
	
	//RECHERCHE UN AGENT DANS LE REPERTOIRE
	public Agent getAgentByName(String agName){
		for (Agent a : repertoire){
			if (a.getLocalName().equals(agName)){
				return a;
			}
			if (a.getLocalName().equals(this.getLocalName()))
				continue;
		}
		System.out.println("Aucun agent nommé "+agName+" !");
		return null;
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
		String[] pos = g[0].split("]");
		
			
		try {
			//IL Y A AU MOINS DEUX NOEUDS DANS LA LISTE
			listvus = pos[0].split(", ");
		} catch (Exception e){
				//S'IL N'Y A QU'UN NOEUD (SPLIT AVEC UN REGEX = ", " ECHOUE)
		}
		
		// SI J'AI DEJA CROISE agName
		if (alreadySent.containsKey(agName)){
			for (String vu : listvus){
				if (!alreadySent.get(agName).contains(vu)){
					update.add(vu);
				}
			}
			
		} else {
			//SI JE NE L'AI JAMAIS VU, AJOUTE UNE NOUVELLE ENTREE			
			update = new ArrayList<String>();
			for (String vu : listvus){
					update.add(vu);
			}
		}
		
		 // AJOUT DES NOEUDS RECUS
		alreadySent.put(agName, update);
		
		Iterator<String> keySetIterator = alreadySent.keySet().iterator();
		while(keySetIterator.hasNext()){
			String cle = keySetIterator.next();
			System.out.println("Agent : " + cle + " J'ai déjà transmis: " + alreadySent.get(cle));
		}
	}
	
	
	//N'ENVOIE QUE LES MESSAGES PAS ENCORE TRANSMIS A SENDER
	public List<String> filtre(List<String> vus, String sender){
		List<String> copyVus = new ArrayList<String>(vus);
		//NOEUDS DEJA ENVOYES A SENDER
		List<String> dejaEnvoye = alreadySent.get(sender);
		System.out.println("CE QUE J'AI DEJA ENVOYE A "+sender+ " : "+dejaEnvoye);
		if (dejaEnvoye != null)
			copyVus.removeAll(dejaEnvoye);
		return copyVus;
		
	}
	


	
	public void echecEnvoi(){
		System.out.println("Nb Essais restants: "+ nbEssai);
		if (nbEssai > 0){
			//System.out.println("Je suis à l'essai "+nbEssai);
			nbEssai--;
		} else { 
			System.out.println("ECHEC DE L'ENVOI");
			block = false;
			nbEssai = 3;
		}
		
	}
	
	protected void beforeMove(){
		super.afterMove();
		System.out.println("BeforeMove");
		addBehaviour(new R1_deployMe(gateKeeperName,this));
	}
	
	protected void afterMove(){
		super.afterMove();
		System.out.println("Je reviens");
		addBehaviour(new R1_deployMe(gateKeeperName,this));
		addBehaviour(new DeployedBehaviour(this));

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
