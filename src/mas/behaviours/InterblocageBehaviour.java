package mas.behaviours;

import java.util.Random;

import mas.agents.ExploAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class InterblocageBehaviour extends OneShotBehaviour  {
	//TODO CAS DU COULOIR
	//PROCEDURE DE TIRAGE AU SORT - LES AGENTS SONT HONNETES,
	//ILS NE REPONDENT PAS EN FONCTION DE LA REPONSE RECUE
		
	private static final long serialVersionUID = 1L;
	private boolean finished=false;

	public InterblocageBehaviour (ExploAgent myagent) {
		super(myagent);
	}
	
	@Override
	public void action() {

			System.out.println("***********InterblocageBehaviour**********");

			System.out.println(((ExploAgent)this.myAgent).getLocalName()+ " INTERBLOCAGE ?");
			
			//MESSAGE GENERAL - BLOQUE ?
			ExploAgent ag = (ExploAgent)this.myAgent;
			String name = ag.getLocalName();
			ACLMessage msg=new ACLMessage(7);
			msg.setSender(ag.getAID());
			
			if (ag.getCurrentPosition() != ""){
				// "B" : "ETES-VOUS BLOQUES ?" L'AGENT TRANSMET SON NOM + LA SALLE VISEE + CHIFFRE RANDOM ENTRE 0 ET 10
				int val = new Random().nextInt(11);
				msg.setContent("B-" + name + "-" + ag.wantedToGo + "-" + val);
				System.out.println(name + " JE TIRE "+val);
				
				// LES DESTINATAIRES SONT TOUS LES AGENTS QUI SONT DANS SON REPERTOIRE.
				// SEULS LES PLUS PROCHES RECEVRONT LE MESSAGE
				for (AID aid : ag.getAgents()){
					msg.addReceiver(aid);
				}
				// ENVOI DU MESSAGE
				ag.sendMessage(msg);

			}
			ag.nbEssaiDeplacement = 0;
			this.finished = true;
		}
}
