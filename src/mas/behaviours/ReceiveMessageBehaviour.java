package mas.behaviours;

import mas.agents.ExploAgent;

import java.util.List;

import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;


public class ReceiveMessageBehaviour extends OneShotBehaviour{
	//J'AJOUTE LES NOEUDS RECUS DANS MA LISTE DE NOEUDS VUS
	private static final long serialVersionUID = 9088209402507795289L;

	private ACLMessage message;

	public ReceiveMessageBehaviour(final ExploAgent myagent, ACLMessage msg) {
		super(myagent);
		this.message = msg;
	}

	public void action() {
		//System.out.println("***********ReceiveMessageBehaviour**********");

		ExploAgent ag = (ExploAgent) this.myAgent;
		String[] data = message.getContent().split("-");
		ag.addVus(data[2]);
		//SI LA LISTE ENVOYEE EST VIDE
				//LISTE VIDE
				if (data[3].equals("[]"))
					return;
				
				//ON RECUPERE LE CONTENU DE LA LISTE (-[])
				String[] listvus = null;
				String[] g = data[3].split("\\[");
				String[] pos = g[1].split("]");
				try {
					//IL Y A AU MOINS DEUX NOEUDS DANS LA LISTE
					listvus = pos[0].split(", ");
				} catch (Exception e){
						//S'IL N'Y A QU'UN NOEUD (SPLIT AVEC UN REGEX = ", " ECHOUE)
				}
				for (int i = 0; i < listvus.length; i++){
					ag.addVus(listvus[i]);
				}
				ag.addVus(data[2]);
			//ENVOI D'UN ACCUSE RECEPTION
			this.myAgent.addBehaviour(new AckBehaviour(ag,message));
		//}
			block();
	}

}

