package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {
    @Override
    public Animal select(Animal a, List<Animal> as) {  //Busca el animal más cercano a a en la lista de animales 
        if (as.isEmpty()) return null;
        Animal closest = as.get(0);
        double minDistance = a.getPosition().distanceTo(closest.getPosition());
        for (Animal target : as) {
            double dist = a.getPosition().distanceTo(target.getPosition());
            if (dist < minDistance) {
                minDistance = dist;
                closest = target;
            }
        }
        return closest;
    }

}