package mas.behaviours;

import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import mas.agents.ExploAgent;

public class ReceiveGeneralBehaviour extends SimpleBehaviour{
	//SELON LE MESSAGE INFORM RECU, DECLENCHE UN BEHAVIOUR
	
	private static final long serialVersionUID = 7458219402507795289L;
	private boolean finished = false;

	public ReceiveGeneralBehaviour(final ExploAgent myagent) {
		super(myagent);
	}

	@Override
	public void action() {

		ExploAgent ag =  (ExploAgent) this.myAgent;
		//ON DEFINIT UN FILTRE
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);		
		final ACLMessage msg = ag.receive(msgTemplate);
		
		//SI ON RECOIT UN MESSAGE
		if (msg != null) {
			//System.out.println("***********ReceiveGeneralBehaviour**********");

			String[] data = msg.getContent().split("-");
			
			//DATA[0] CONTIENT TOUJOURS LE TYPE DE MESSAGE
			switch (data[0]){
			case "MSG": //JE RECOIS DES DONNEES
				System.out.println("<--------"+ag.getLocalName()+": J'AI RECU UNE POSITION DE "+msg.getSender().getLocalName()+" CONTENT = "+msg.getContent());
				ag.addBehaviour(new ReceiveMessageBehaviour(ag,msg));
				//J'AI BIEN RECU, JE ME DEBLOQUE
				break;
			case "OK": //JE RECOIS UN ACCUSE RECEPTION
				System.out.println("<--------"+ag.getLocalName()+" : J'AI RECU UN ACCUSE RECEPTION DE "+msg.getSender().getLocalName()+" CONTENT = "+msg.getContent());
				//MON MESSAGE A ETE RECU, JE ME DEBLOQUE
				ag.addBehaviour(new ReceiveAckBehaviour(ag,msg));
				break;
			case "?": //JE RECOIS UN "APPEL"
				System.out.println("<--------"+ag.getLocalName()+" : J'AI RECU UNE QUESTION DE "+msg.getSender().getLocalName()+" CONTENT = "+msg.getContent());
				//JE M'ARRETE
				ag.block = true;
				ag.addBehaviour(new AnswerToSayHello(ag,msg.getSender(),data[2]));
				break;
			case "!": //J'AI RECU UNE REPONSE À MON "APPEL"
				System.out.println("<--------"+ag.getLocalName()+" : J'AI RECU UNE REPONSE DE "+msg.getSender().getLocalName()+" CONTENT = "+msg.getContent());
				ag.addBehaviour(new MyPositionBehaviour(ag,msg.getSender().getLocalName()));
				break;
			case "B": // "ETES-VOUS BLOQUES ?", QUESTION D'UN AGENT EN SITUATION D'INTERBLOQUAGE
				System.out.println("<--------"+ag.getLocalName()+" : BLOQUE ? DE "+msg.getSender().getLocalName()+" CONTENT = "+msg.getContent());
				ag.addBehaviour(new AnswerInterBehaviour(ag, msg));
				break;
			case "Breponse": //RESULTAT DU TIRAGE AU SORT EN CAS D'INTERBLOCAGES
				System.out.println("<--------"+ag.getLocalName()+" : RESULTAT TIRAGE AU SORT DE "+msg.getSender().getLocalName()+" CONTENT = "+msg.getContent());
				ag.interblocage = false;
				if (data[1].equals("false")){ //L'AUTRE AGENT NE RECULE PAS, DONC C'EST MOI
					ag.addBehaviour(new ReculerBehaviour(ag));
				}
				break;
			}
		} else {
			ag.block = true;
			//SI JE SUIS EN SITUATION D'INTERBLOCAGE
			if (ag.interblocage){
				ag.addBehaviour(new InterblocageBehaviour(ag));
				return;
			}
			ag.addBehaviour(new SayHelloBehaviour(ag));
			// JE ME BLOQUE EN TENTANT DE COMMUNIQUER
			ag.echecEnvoi();
			block();
		}
		finished = true;
		
	}
	
	public boolean done(){
		return finished;
	}


}
