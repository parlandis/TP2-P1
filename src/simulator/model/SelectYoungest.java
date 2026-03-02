package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy {
    @Override
    public Animal select(Animal a, List<Animal> as) { 
        if (as.isEmpty()) return null;
        Animal youngest = as.get(0);
        for (Animal target : as) {
            if (target.getAge() < youngest.getAge()) {
                youngest = target;
            }
        }
        return youngest;
    }
}
