package simulator.model;
import java.util.List;


public interface SelectionStrategy {    // TODO: NO entiendo donde coño hacer las estas
	Animal select(Animal a, List<Animal> as);  
}