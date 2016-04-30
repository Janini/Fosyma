package mas.behaviours;

import java.util.List;

import env.Attribute;
import env.Couple;
import mas.agents.ExploAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;

public class ReculerBehaviour extends OneShotBehaviour {
	//EN CAS D'INTERBLOCAGE, CONSISTE A RECULER JUSQU'A TROUVER UNE NOUVELLE POSITION
	private static final long serialVersionUID = 1L;

	public ReculerBehaviour(ExploAgent a) {
		super(a);
	}


	@Override
	public void action() {
		ExploAgent ag = (ExploAgent) this.myAgent;
		List<Couple<String,List<Attribute>>> lobs=ag.observe();
		lobs.remove(0); //J'ENLEVE LA POSITION ACTUELLE
		int j = 0;
		//IL NE VA PLUS SUR LE NOEUD INATTEIGNABLE
		while (j < lobs.size()){
			if ((lobs.get(j).getLeft()).equals(ag.wantedToGo)){
				lobs.remove(lobs.get(j));
				break;
			} else j++;
		}
		if (!lobs.isEmpty()){
			String lastPos = lobs.get(0).getLeft();
			System.out.println(ag.getLocalName()+" : Je vais en "+lastPos);
			ag.moveTo(lastPos);
		}
		//ExploAgent.block = false;
	}
	
}
