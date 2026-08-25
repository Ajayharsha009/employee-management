package com.ajay.employee_management.service;
import org.springframework.stereotype.Service;
import com.ajay.employee_management.repository.EmployeeRepository;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ajay.employee_management.dto.DepartmentStatistics;

import com.ajay.employee_management.entity.Employee;
import java.util.NoSuchElementException;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
  
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
     
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
     
    public Employee updateEmployee(Integer id, Employee employee) {

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Employee not found with id: " + id));

        existingEmployee.setName(employee.getName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setSalary(employee.getSalary());

        return employeeRepository.save(existingEmployee);
    }
     
    public void deleteEmployee(Integer id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Employee not found with id: " + id));

        employeeRepository.delete(employee);
    }
    
    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Employee not found with id: " + id));
    }
    
    public List<Employee> searchEmployeesByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Employee> searchEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartmentIgnoreCase(department);
    }
     
    public List<Employee> searchEmployeesBySalary(
            BigDecimal minSalary, BigDecimal maxSalary) {

        return employeeRepository.findBySalaryBetween(minSalary, maxSalary);
    }
    
    public List<Employee> filterEmployees(
            String department,
            BigDecimal minSalary,
            BigDecimal maxSalary) {

        return employeeRepository.findByDepartmentIgnoreCaseAndSalaryBetween(
                department, minSalary, maxSalary);
    }
    
    public List<Employee> sortEmployeesBySalary(String direction) {

        Sort sort;

        if (direction.equalsIgnoreCase("asc")) {
            sort = Sort.by("salary").ascending();
        } else if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by("salary").descending();
        } else {
            throw new IllegalArgumentException(
                    "Direction must be 'asc' or 'desc'");
        }

        return employeeRepository.findAll(sort);
    }
     
    public Page<Employee> getEmployeesWithPagination(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }
    
    public long countEmployeesByDepartment(String department) {
        return employeeRepository.countByDepartmentIgnoreCase(department);
    }
    
    public long countEmployeesBySalary(
            BigDecimal minSalary, BigDecimal maxSalary) {

        return employeeRepository.countBySalaryBetween(
                minSalary, maxSalary);
    }
    
    public Employee getHighestPaidEmployee() {
        return employeeRepository.findTopByOrderBySalaryDesc();
    }
    
    public Employee getLowestPaidEmployee() {
        return employeeRepository.findTopByOrderBySalaryAsc();
    }
    
    public BigDecimal getAverageSalary() {
        return employeeRepository.findAverageSalary();
    }
    
    public DepartmentStatistics getDepartmentStatistics(String department) {

        long employeeCount =
                employeeRepository.countByDepartmentIgnoreCase(department);

        BigDecimal averageSalary =
                employeeRepository.findAverageSalaryByDepartment(department);

        BigDecimal highestSalary =
                employeeRepository.findHighestSalaryByDepartment(department);

        BigDecimal lowestSalary =
                employeeRepository.findLowestSalaryByDepartment(department);

        return new DepartmentStatistics(
                department,
                employeeCount,
                averageSalary,
                highestSalary,
                lowestSalary);
    }
}