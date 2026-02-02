package simulator.model;

import java.util.List;
//TODO: Cambiarlas cuando sepa usar Clases Anidadas
public class SelectFirst implements SelectionStrategy {
    @Override
    public Animal select(Animal a, List<Animal> as) {
        return as.isEmpty() ? null : as.get(0);
    }
}
