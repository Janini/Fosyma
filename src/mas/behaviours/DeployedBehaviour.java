package mas.behaviours;

import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import mas.agents.ExploAgent;
import mas.agents.ExploAgentBis;

public class DeployedBehaviour extends SimpleBehaviour{
	
	private static final long serialVersionUID = 7458214402507795289L;

	private boolean finished = false;

	public DeployedBehaviour(final ExploAgentBis myagent) {
		super(myagent);
	}

	@Override
	public void action() {
		ExploAgentBis ag =  (ExploAgentBis) this.myAgent;
		//ON DEFINIT UN FILTRE
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			
		final ACLMessage msg = ag.receive(msgTemplate);
		
		//SI ON RECOIT UN MESSAGE
		if (msg != null) {
			String data = msg.getContent();
			System.out.println(data);
			//"JE SUIS REVENU"
			if (data.equals("I'm deployed")){
				System.out.println("OK");
			}
			
			ag.addBehaviour(new MoveBehaviourBis(ag));
			//TODO POUR MIGRATION
			//ag.addBehaviour(new UpdateMapBehaviour(ag));
			this.finished=true;
		} else {
			block();
		}
		
	}

	@Override
	public boolean done() {
		return finished;
	}
}
