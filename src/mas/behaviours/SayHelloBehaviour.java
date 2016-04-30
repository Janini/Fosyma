package mas.behaviours;

import mas.agents.ExploAgent;
import mas.agents.InitAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class SayHelloBehaviour extends OneShotBehaviour {
	
	//TENTE D'IDENTIFIER LES AGENTS A PROXIMITE POUR SAVOIR QUOI LEUR ENVOYER	
	private static final long serialVersionUID = -2058134104078521998L;
	private boolean finished=false;

	
	public SayHelloBehaviour (final ExploAgent myagent) {
		super(myagent);
	}
	
	
	@Override
	public void action() {
			System.out.println("***********SayHelloBehaviour**********");
			ExploAgent ag = (ExploAgent)this.myAgent;
			String name = ag.getLocalName();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setSender(ag.getAID());
			
			if (ag.getCurrentPosition() != ""){
	
				// "?" CORRESPOND A UNE QUESTION ENVOYEE. L'AGENT TRANSMET SON NOM + LE POINT DE RDV 
				msg.setContent("?-" + name + "-" + ag.getRdv());
				
				// LES DESTINATAIRES SONT TOUS LES AGENTS QUI SONT DANS SON REPERTOIRE.
				// SEULS LES PLUS PROCHES RECEVRONT LE MESSAGE
				for (AID aid : ag.getAgents()){
					msg.addReceiver(aid);
				}
				// ENVOI DU MESSAGE
				ag.sendMessage(msg);
			}
			block();
		}
//	}
	
//	public boolean done(){
//		return finished;
//	}

}
