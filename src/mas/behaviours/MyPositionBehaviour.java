package mas.behaviours;

import java.util.List;

import mas.agents.ExploAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class MyPositionBehaviour extends OneShotBehaviour{
	//J'ENVOIE CE QUE JE SAIS SELON L'EXPEDITEUR
	//RECHERCHE DANS LA MAP
	
	private static final long serialVersionUID = -2058134622078521998L;
	private String sender;
	
	public MyPositionBehaviour (final ExploAgent myagent, String sender) {
		super(myagent);
		this.sender = sender;
	}

	@Override
	public void action() {
		System.out.println("***********MyPositionBehaviour**********");

		ExploAgent ag = (ExploAgent)this.myAgent;
		String myPosition = ag.getCurrentPosition();
		String name = ag.getLocalName();

		ACLMessage msg = new ACLMessage(7);
		msg.setSender(ag.getAID());

		if (myPosition!=""){
			//FAIT L'INTERSECTION ENTRE CE QU'IL SAIT ET CE QU'IL A DEJA DIT A sender
			List<String> listeFiltre = ag.filtre(ag.getVus(), sender);
			if (listeFiltre.isEmpty()){
				ag.block = false;
				return;
			}
			System.out.println(name + " : J'ENVOIE " + listeFiltre);			
			//MSG - MonNom - MaPosition - CeQueJeDoisTransmettre 
			msg.setContent("MSG-"+name+"-"+myPosition+"-"+listeFiltre);
			for (AID aid : ag.getAgents()){
					msg.addReceiver(aid);
			}
			ag.sendMessage(msg);
		}
		block();
	}
}