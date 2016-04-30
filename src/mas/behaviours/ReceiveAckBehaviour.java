package mas.behaviours;

import mas.agents.ExploAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiveAckBehaviour extends OneShotBehaviour {
	// JE METS A JOUR MON MAPPING : JE SAIS QUE MON DESTINATAIRE A BIEN RECU MON MESSAGE, PLUS BESOIN DE LUI REPETER
	private static final long serialVersionUID = 9088209402507795289L;

	private ACLMessage msg;

	public ReceiveAckBehaviour(final ExploAgent myagent, ACLMessage msg) {
		super(myagent);
		this.msg = msg;
	}


	public void action() {
		String[] id = msg.getContent().split("-");
		//AJOUT DES POSITIONS RECUES DANS MA MAP
		((ExploAgent) this.myAgent).majSend(msg.getSender().getLocalName(), id[4]);
		((ExploAgent) this.myAgent).block = false;
		block();
	}

}
