package mas.behaviours;

import jade.core.behaviours.SimpleBehaviour;
import jade.util.leap.Iterator;

import java.util.List;
import java.util.Random;

import mas.agents.ExploAgent;

import org.graphstream.algorithm.AStar;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;

import princ.Principal;
import env.Attribute;
import env.Couple;
//import env.Environment.Couple;

public class MoveBehaviour extends SimpleBehaviour{
	
	private static final long serialVersionUID = 4088209402507795289L;
	private boolean finished = false;
	
	public MoveBehaviour (ExploAgent myagent) {
		super(myagent);
	}

	@Override
	public void action() {
		String myPosition=((ExploAgent)this.myAgent).getCurrentPosition();
		ExploAgent ag = (ExploAgent) this.myAgent;
		if (ag.block || ag.interblocage){
			System.out.println(ag.getLocalName()+ " : Je suis bloqué ");
			return;
		}
		ag.resetInterblocage();
			
		if (myPosition!=""){
			//Liste des cases atteignables
			List<Couple<String,List<Attribute>>> lobs = ag.observe();
			for (Couple<String, List<Attribute>> c : lobs){
				ag.obs.add(c.getLeft());
			}
			List<String> vus = ag.getVus();
			List<String> path = ag.getPath();
			
			
			//Position initiale de l'agent
			if (path.isEmpty()){
				try {
					ag.setStart(myPosition);
					//TODO GRID

					synchronized(ExploAgent.lock){
					
						if (Principal.type.equals("Grid")){
							String[] split = myPosition.split("_");
							Node n = Principal.graph.addNode(myPosition);
							
							n.addAttribute("x", split[0]);
							n.addAttribute("y", split[1]);
							n.addAttribute("ui.label", split[0]+","+split[1]);
							
							System.out.println("CREATION NOEUD "+myPosition);
						}
	
						//TODO AUTRES
						if (Principal.type.equals("mapCouloir")){
							Node n = Principal.graph.addNode(myPosition);
		
							n.addAttribute("x", myPosition);
							n.addAttribute("ui.label", myPosition);
						}
						
						try{
							Thread.sleep(100);
						} catch(Exception e){
							
						}
					
					}
				} catch (IdAlreadyInUseException e) {
					e.getStackTrace();
				}
					
			}
		
			if (!vus.contains(myPosition))
				vus.add(myPosition);
			if ((path.size() == 0)||(!path.get(path.size()-1).equals(myPosition)))
				path.add(myPosition);
			
			//list of attribute associated to the currentPosition
			List<Attribute> lattribute= lobs.get(0).getRight();
			//example related to the use of the backpack for the treasure hunt
			Boolean b=false;
			for(Attribute a:lattribute){
				switch (a) {
				case TREASURE:
					System.out.println("My current backpack capacity is:"+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
					System.out.println("Value of the treasure on the current position: "+a.getValue());
					//System.out.println("The agent grabbed :"+((mas.abstractAgent)this.myAgent).pick());
					//System.out.println("the remaining backpack capacity is: "+ ((mas.abstractAgent)this.myAgent).getBackPackFreeSpace());
					//System.out.println("The value of treasure on the current position: (unchanged before a new call to observe()): "+a.getValue());
					ag.listTresors.put(myPosition, (int) a.getValue());
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
			
			
			remove(lobs,vus);
				
			if (lobs.isEmpty()){
				int ind = path.indexOf(myPosition);

				//SI JE CONNAIS DEJA TOUT CE QUE J'AI OBSERVE -> FIN EXPLORATION
				boolean finExploration = true;
				Iterator i = ag.obs.iterator();
				while (i.hasNext()){
					String salle = i.next().toString();
					if (!vus.contains(salle)){
						finExploration = false;
						break;
					}
				}
				if (finExploration){
					System.out.println(ag.getLocalName()+ " : J'ai fini ! Les trésors que j'ai vu : "+ag.listTresors);// Ce que j'ai parcouru "+ag.getPath()+ ", ce que je sais "+ ag.getVus()+", je dois me rendre en " + ag.getRdv());
					
					AStar astar = new AStar(Principal.graph);
					astar.compute("0_0", ag.getStart());
					System.err.println(astar.getShortestPath());
					
					ag.doWait();
					return;
				}
				
				((mas.abstractAgent)this.myAgent).moveTo(path.get(ind-1));
				return;
			}
			

	
				//Little pause to allow you to follow what is going on
				/*try {
					System.out.println("Press a key to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
					System.in.read();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				
			
			Random r= new Random();
			int moveId=r.nextInt(lobs.size());
			
			//MAJ DE LA DERNIERE POSITION
			ag.setLastPos(ag.getPath().get(ag.getPath().size()-1));
			
			
			//L'AGENT ESSAYE DE SE DEPLACER
			if (!ag.moveTo(lobs.get(moveId).getLeft())){
				ag.wantedToGo = lobs.get(moveId).getLeft();
				System.out.println(ag.getLocalName()+" : Un agent se trouve ici, en " + ag.wantedToGo + " !");
				ag.echecDeplacement();
			}
		}
		finished = true;
	}
	
	//ON ENLEVE LES CASES DEJA VUES DE LA LISTE DES CASES ACCESSIBLES
	public void remove(List<Couple<String,List<Attribute>>> lobs, List<String> vus){
		int j = 0;
		while (j < lobs.size()){
			if (vus.contains(lobs.get(j).getLeft())){
				lobs.remove(lobs.get(j));
			} else j++;
		}
	}
	
	public boolean done(){
		return finished;
	}
 }

	