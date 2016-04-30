package mas.behaviours;

import java.util.List;
import java.util.Random;

import org.graphstream.graph.Node;

import princ.Principal;
import mas.agents.ExploAgentBis;
import env.Attribute;
import env.Couple;
//import env.Environment.Couple;
import jade.core.behaviours.TickerBehaviour;

/**************************************
 * 
 * 
 * 				BEHAVIOUR (ne marche que sur des ExploAgentBis)
 * 
 * 
 **************************************/


public class MoveBehaviourBis extends TickerBehaviour{
	/**
	 * When an agent choose to move
	 *  
	 */
	private static final long serialVersionUID = 4088209402507795289L;
	
	
	public MoveBehaviourBis (ExploAgentBis myagent) {
		super(myagent, 100);
		//super(myagent);
	}

	@Override
	public void onTick() {

		
		//Position courante
		String myPosition=((ExploAgentBis)this.myAgent).getCurrentPosition();

		if (myPosition!=""){

			
			//Liste des cases atteignables
			List<Couple<String,List<Attribute>>> lobs=((ExploAgentBis)this.myAgent).observe();//myPosition
			ExploAgentBis ag = (ExploAgentBis) this.myAgent;
			List<String> vus = ag.getVus();
			List<String> path = ag.getPath();
			//Position initiale de l'agent
			if (path.isEmpty()){
				ag.setStart(myPosition);
				String[] split = myPosition.split("_");
				Node n = Principal.graph.addNode(myPosition);
				
				// A CHANGER, GRID
					
				n.addAttribute("x", split[0]);
				n.addAttribute("y", split[1]);
				

				n.addAttribute("ui.label", split[0]+","+split[1]);
			}
			
	
			if (!vus.contains(myPosition))
				vus.add(myPosition);
			if ((path.size() == 0)||(!path.get(path.size()-1).equals(myPosition)))
				path.add(myPosition);
			//System.out.println("Je suis "+this.myAgent.getLocalName()+" en "+myPosition+" !");
			//System.out.println(this.myAgent.getLocalName()+" : J'ai deje vu : "+vus+" et mon trajet est "+path);
			remove(lobs,vus);
			//System.out.println(this.myAgent.getLocalName()+" : cases accessibles: "+lobs);
			
			if (lobs.isEmpty()){
				int ind = path.indexOf(myPosition);
				if (ind == 0){
					System.out.println("J'ai fini !");
					return;
				}
				((mas.abstractAgent)this.myAgent).moveTo(path.get(ind-1));
				return;
			}
			//System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);
			
			

			//Little pause to allow you to follow what is going on
			/*try {
				System.out.println("Press a key to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//list of attribute associated to the currentPosition
			List<Attribute> lattribute= lobs.get(0).getRight();
			
			//example related to the use of the backpack for the treasure hunt
			Boolean b=false;
			for(Attribute a:lattribute){
				switch (a) {
				case TREASURE:
					System.out.println("My current backpack capacity is:"+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
					System.out.println("Value of the treasure on the current position: "+a.getValue());
					System.out.println("The agent grabbed :"+((mas.abstractAgent)this.myAgent).pick());
					System.out.println("the remaining backpack capacity is: "+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
					System.out.println("The value of treasure on the current position: (unchanged before a new call to observe()): "+a.getValue());
					b=true;
					break;

				default:
					break;
				}
			}

			//If the agent picked (part of) the treasure
			if (b){
				List<Couple<String,List<Attribute>>> lobs2=((mas.abstractAgent)this.myAgent).observe();//myPosition
				System.out.println("lobs after picking "+lobs2);
			}

			
			Random r= new Random();
			int moveId=r.nextInt(lobs.size());
			
			//MAJ DE LA DERNIERE POSITION
			ag.setLastPos(ag.getPath().get(ag.getPath().size()-1));
			
			
			//L'AGENT ESSAYE DE SE DEPLACER
			if (!ag.moveTo(lobs.get(moveId).getLeft())){
				System.out.println(ag.getLocalName()+" : Un agent se trouve ici !");
				//TODO DES BEHAVIOURS POUR SE METTRE D'ACCORD SUR CELUI QUI RECULE 			
			}
		}
		
		
	}
	
	//Enlever les cases deje vues de la liste des cases accessibles
	public void remove(List<Couple<String,List<Attribute>>> lobs, List<String> vus){

		int j = 0;
		while (j < lobs.size()){
			if (vus.contains(lobs.get(j).getLeft())){
				lobs.remove(lobs.get(j));
			} else j++;
		}
	}
 }