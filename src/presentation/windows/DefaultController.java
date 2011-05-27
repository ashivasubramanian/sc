package presentation.windows;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import calculations.data_access.Employee;

@Controller()
public class DefaultController {
	
	@Autowired
	SessionFactory sessionFactory;
	
	@RequestMapping("/hi")
	public ModelAndView root() {
		System.out.println("in the method...");
		System.out.println("session: " + sessionFactory.getCurrentSession());
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		System.out.println("sql query size: " + session.createSQLQuery("select * from employee")
				.list().size());
		List<Employee> list = session.createCriteria(Employee.class).list();
		System.out.println("list.size(): " + list.size());
		for (Employee e : list) {
			System.out.println(e.getName());
		}
		transaction.commit();
		return new ModelAndView("index");
	}

}
