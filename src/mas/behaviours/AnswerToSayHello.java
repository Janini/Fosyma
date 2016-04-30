package mas.behaviours;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import mas.agents.ExploAgent;

public class AnswerToSayHello extends OneShotBehaviour{
	
	//L'AGENT DECLINE SON IDENTITE APRES AVOIR RECU UN "HELLO"
	private static final long serialVersionUID = -2858678625078524918L;
	private AID sender;
	private String rdv;
	
	public AnswerToSayHello(final ExploAgent a, AID sender, String rdv) {
		super(a);
		this.sender = sender;
		this.rdv = rdv;
	}

	@Override
	public void action() {
		ExploAgent ag = (ExploAgent) this.myAgent;
		String myPosition = ag.getCurrentPosition();
		ACLMessage msg = new ACLMessage(7);
		msg.setSender(ag.getAID());

		if (myPosition!=""){
			ag.setRdv(rdv);
			msg.setContent("!-"+ag.getLocalName());
			msg.addReceiver(sender);
			ag.sendMessage(msg);
		}
		block();
	}
}