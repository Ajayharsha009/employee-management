package com.ajay.employee_management.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ajay.employee_management.entity.Employee;
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	
	List<Employee> findByNameContainingIgnoreCase(String name);
	
	List<Employee> findByDepartmentIgnoreCase(String department); 
	
	List<Employee> findBySalaryBetween(BigDecimal minSalary, BigDecimal maxSalary);
	
	List<Employee> findByDepartmentIgnoreCaseAndSalaryBetween(
	        String department,
	        BigDecimal minSalary,
	        BigDecimal maxSalary);
	
	long countByDepartmentIgnoreCase(String department);
	
	long countBySalaryBetween(BigDecimal minSalary, BigDecimal maxSalary);
	
	Employee findTopByOrderBySalaryDesc();
	
	Employee findTopByOrderBySalaryAsc();
	
	@Query("SELECT AVG(e.salary) FROM Employee e")
	BigDecimal findAverageSalary();
	
	@Query("SELECT AVG(e.salary) FROM Employee e WHERE LOWER(e.department) = LOWER(:department)")
	BigDecimal findAverageSalaryByDepartment(String department);

	@Query("SELECT MAX(e.salary) FROM Employee e WHERE LOWER(e.department) = LOWER(:department)")
	BigDecimal findHighestSalaryByDepartment(String department);

	@Query("SELECT MIN(e.salary) FROM Employee e WHERE LOWER(e.department) = LOWER(:department)")
	BigDecimal findLowestSalaryByDepartment(String department);

}