package mas.behaviours;

import java.util.Random;

import mas.agents.ExploAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class AnswerInterBehaviour extends OneShotBehaviour {
	//TIRE UNE VALEUR AU HASARD + COMPARAISON AVEC VALEUR RECUE +
	//JE SAIS QUE SI J'AI UNE VALEUR < JE DOIS RECULER + RENVOIE SA
	//CONCLUSION + AGIT EN CONSEQUENCE
	private static final long serialVersionUID = 2L;

	private ACLMessage msg;
	public AnswerInterBehaviour(ExploAgent a, ACLMessage msg) {
		super(a);
		this.msg = msg;
	}

	@Override
	public void action() {
		System.out.println("***********AnswerInterBehaviour**********");
		
		boolean jeRecule = false;
		ExploAgent ag = (ExploAgent) this.myAgent;
		ag.nbEssaiDeplacement = 0;
		ag.interblocage = false;
		//JE NE REPONDS QUE SI JE SUIS BLOQUE (DONC DANS LA SALLE SOUHAITEE PAR L'AUTRE AGENT)
			String[] data = msg.getContent().split("-");
			if (!data[2].equals(ag.getCurrentPosition())){
				return;
			}
			AID sender = msg.getSender();
			if (data[3] != null){
				int value = Integer.parseInt(data[3]);
				//SI JE TIRE UNE VALEUR PLUS PETITE, JE RECULE
				if (new Random().nextInt(11) < value){
					jeRecule = true;
				}
				msg.setContent("Breponse-"+jeRecule);
				System.out.println(ag.getName()+" "+msg.getContent());
				msg.addReceiver(sender);
				
				// ENVOI DU MESSAGE
				ag.sendMessage(msg);
				
				//TODO : SI JERECULE = TRUE JE RECULE (BEHAVIOUR) !
				if (jeRecule)
					ag.addBehaviour(new ReculerBehaviour(ag));
			}
	}
}
