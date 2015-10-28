package restaurant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu {
	List<String> foods = new ArrayList<String>();
	Map<String, Float> Prices = new HashMap<String, Float>();
	
	public Menu(){
		foods.add("Steak");
		foods.add("Chicken");
		foods.add("Salad");
		foods.add("Pizza");
		
		Prices.put("Steak", (float) 15.99);
		Prices.put("Chicken", (float) 10.99);
		Prices.put("Pizza", (float) 8.99);
		Prices.put("Salad", (float) 5.99);
	}
	
	public void remove(String food){
		foods.remove(food);
	}
	public boolean find(String name){
		for(String f : foods)
		{
			if(name.equals(f))
				return true;
		}
		return false;
	}
	public String randomSelect(){
			int hackChoice = (int) (Math.random() * foods.size()-1);
			return foods.get(hackChoice);
	}
}
