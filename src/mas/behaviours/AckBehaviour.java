package mas.behaviours;

import mas.agents.ExploAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class AckBehaviour extends OneShotBehaviour {
	private ACLMessage message;
	
	public AckBehaviour(final ExploAgent a, ACLMessage msg) {
		super(a);
		this.message = msg;
	}

	private static final long serialVersionUID = -2858696622078524998L;
	
	@Override
	public void action() {
		ExploAgent ag = (ExploAgent) this.myAgent;
		String myPosition = ag.getCurrentPosition();
		ACLMessage msg2 = new ACLMessage(7);
		msg2.setSender(ag.getAID());

		if (myPosition!=""){
			//MESS = MSG-NOMAGENT-MAPOSITION-LISTVUS
			String mess = message.getContent();
			msg2.setContent("OK-"+mess);
			msg2.addReceiver(message.getSender());
			ag.sendMessage(msg2);
		}
		ag.block = false;
	}
}
