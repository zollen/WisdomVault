import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LambdaExercises {


	public static void main(String [] args) {


		doit(() -> { System.out.println("HELLO"); });

		Predicate<Person> adults = p -> p.getAge() > 18;

	

		Person p1 = new Person("Stephen", 20, "Math");
		Person p2 = new Person("King", 16, "Computer");
		Person p3 = new Person("James", 46, "Math");
		Person p4 = new Person("Mary", 13, "Computer");

		List<Person> people = new ArrayList<Person>();
		people.add(p1); people.add(p2); people.add(p3); people.add(p4);

		people.parallelStream().filter(adults).forEach( p -> { System.out.println(p.getName()); } );

		System.out.println("TOTAL: " + people.stream().filter(adults).mapToInt( p -> p.getAge() ).sum());

		Person pf = people.parallelStream().filter(adults).distinct().findFirst().orElse(null);
		System.out.println("FOUND: " + pf);

		List<Person> targets = people.parallelStream().filter(adults).distinct().collect(Collectors.toList());

		if (targets != null)
			targets.stream().forEach(p -> { System.out.println("GOT IT: " + p); });


		Function<Person, String> praise = p -> { return p.getName() + " COOL!!!"; };

		people.stream().forEach( p -> { System.out.println(p.doit(praise)); }  );

		Cat<String> cat1 = (c, d) -> { System.out.println(c + d); };

		cat1.doit( "SUPERMAN", " IS AWESOME!!" );

		Cat<Integer> cat2 = (c, d) -> { System.out.println(c + d); };

		cat2.doit( 5, 10 );

		String all = null;

		all = people.stream().map( Person::getName ).collect(Collectors.joining(", "));
		System.out.println("NAMES: " + all);

		all = people.stream().map(Object::toString).collect(Collectors.joining(", "));
		System.out.println("NAMES: " + all);

		int totalAge = people.stream().mapToInt( Person::getAge ).sum();
		System.out.println("TOTAL AGE: " + totalAge);
		totalAge = people.stream().collect(Collectors.summingInt(Person::getAge));
		System.out.println("TOTAL AGE: " + totalAge);

		Map<String, List<Person>> byDept =
			people.stream().collect(Collectors.groupingBy(Person::getDept));


		byDept.entrySet().stream()
			.forEach (e -> { 
					System.out.println("[" + e.getKey() + "]"); 
					e.getValue().stream().forEach( p -> { System.out.println(" ==> " + p); }); 
					} 
				);

		Map<String, Integer> ageByDept =
			people.stream().collect(Collectors.groupingBy(Person::getDept, Collectors.summingInt(Person::getAge)));

		
		ageByDept.entrySet().stream()
			.forEach(e -> {
					System.out.println("dept: [" + e.getKey() + "] ==> total: [" + e.getValue() + "]");
				});

		Map<Boolean, List<Person>> adultsOrNot =
			people.stream().collect(Collectors.partitioningBy(adults));

		adultsOrNot.entrySet().stream()
			.forEach(e -> {
					System.out.println("Condition: [" + e.getKey() + "]");
					e.getValue().stream().forEach(p -> { 
									System.out.println(" ==> " + p.getName());
									}
								);
					}
				);

		totalAge = people.stream().mapToInt(Person::getAge).reduce(0, (a,b) -> a * 2 + b );
		System.out.println("Crazy Age: " + totalAge);
		
		double [] input = { 1.0, 2.3, 4.5, 5.6 };
		
		Arrays.stream(input).mapToObj( Double::valueOf )
	      .forEach(System.out::println);


	}

	public static void doit(Robot robot) {
		robot.doit();
	}

	public static class Person {

		private int age = 0;
		private String name = "";
		private String dept = "";

		public Person() {}

		public Person(String name, int age, String dept) {
			this.name = name;
			this.age = age;
			this.dept = dept;
		}

		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}	

		public String getDept() {
			return dept;
		}

		public void setDept(String dept) {
			this.dept = dept;
		}

		public String doit(Function<Person, String> f) {
			return f.apply(this);
		}

		public String toString() {
			return "Name: [" + name + "] Age: [" + age + "] Dept: [" + dept + "]";
		}

	}

}
