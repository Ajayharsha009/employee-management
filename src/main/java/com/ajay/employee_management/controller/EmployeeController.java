package com.ajay.employee_management.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.ajay.employee_management.entity.Employee;
import com.ajay.employee_management.service.EmployeeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import com.ajay.employee_management.dto.DepartmentStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(
            summary = "Get all employees",
            description = "Retrieves a list of all employees."
    )
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
    
    @Operation(
            summary = "Search employees by name",
            description = "Finds employees whose names contain the given search text."
    )
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @GetMapping("/search")
    public List<Employee> searchEmployees(@RequestParam String name) {
        return employeeService.searchEmployeesByName(name);
    }
    
    @Operation(
            summary = "Search employees by department",
            description = "Finds all employees belonging to the specified department."
    )
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @GetMapping("/search/department")
    public List<Employee> searchEmployeesByDepartment(
            @RequestParam String department) {

        return employeeService.searchEmployeesByDepartment(department);
    }
    
    @Operation(
            summary = "Search employees by salary range",
            description = "Finds employees whose salary falls between the specified minimum and maximum values."
    )
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @GetMapping("/search/salary")
    public List<Employee> searchEmployeesBySalary(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {

        return employeeService.searchEmployeesBySalary(min, max);
    }
    
    @Operation(
            summary = "Sort employees by salary",
            description = "Returns employees sorted by salary in ascending or descending order."
    )
    @ApiResponse(responseCode = "200", description = "Employees sorted successfully")
    @ApiResponse(responseCode = "400", description = "Direction must be 'asc' or 'desc'")
    @GetMapping("/sort/salary")
    public List<Employee> sortEmployeesBySalary(
            @RequestParam String direction) {

        return employeeService.sortEmployeesBySalary(direction);
    }
    
    @Operation(
            summary = "Filter employees",
            description = "Finds employees in a specific department whose salary falls within the given range."
    )
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @GetMapping("/filter")
    public List<Employee> filterEmployees(
            @RequestParam String department,
            @RequestParam BigDecimal minSalary,
            @RequestParam BigDecimal maxSalary) {

        return employeeService.filterEmployees(
                department, minSalary, maxSalary);
    }
    
    @Operation(
            summary = "Get employees with pagination and sorting",
            description = "Returns employees page by page and allows sorting by a selected field in ascending or descending order."
    )
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid pagination or sorting parameters")
    @GetMapping("/page")
    public Page<Employee> getEmployeesWithPagination(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String direction) {

        Sort sort;

        if (direction.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        return employeeService.getEmployeesWithPagination(pageable);
    }
    
    @Operation(
            summary = "Count employees by department",
            description = "Returns the number of employees belonging to the specified department."
    )
    @ApiResponse(responseCode = "200", description = "Employee count retrieved successfully")
    @GetMapping("/count/department")
    public long countEmployeesByDepartment(
            @RequestParam String department) {

        return employeeService.countEmployeesByDepartment(department);
    }
    
    @Operation(
            summary = "Count employees by salary range",
            description = "Returns the number of employees whose salary falls within the specified range."
    )
    @ApiResponse(responseCode = "200", description = "Employee count retrieved successfully")
    @GetMapping("/count/salary")
    public long countEmployeesBySalary(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {

        return employeeService.countEmployeesBySalary(min, max);
    }
    
    @Operation(
            summary = "Get highest-paid employee",
            description = "Returns the employee with the highest salary."
    )
    @ApiResponse(responseCode = "200", description = "Highest-paid employee retrieved successfully")
    @GetMapping("/highest-salary")
    public Employee getHighestPaidEmployee() {
        return employeeService.getHighestPaidEmployee();
    }
    
    
    @Operation(
            summary = "Get lowest-paid employee",
            description = "Returns the employee with the lowest salary."
    )
    @ApiResponse(responseCode = "200", description = "Lowest-paid employee retrieved successfully")
    @GetMapping("/lowest-salary")
    public Employee getLowestPaidEmployee() {
        return employeeService.getLowestPaidEmployee();
    }
    
    @Operation(
            summary = "Get average salary",
            description = "Returns the average salary of all employees."
    )
    @ApiResponse(responseCode = "200", description = "Average salary retrieved successfully")
    @GetMapping("/average-salary")
    public BigDecimal getAverageSalary() {
        return employeeService.getAverageSalary();
    }
    
    @Operation(
            summary = "Get department salary statistics",
            description = "Returns employee count, average salary, highest salary, and lowest salary for a department."
    )
    @ApiResponse(
            responseCode = "200",  description = "Department statistics retrieved successfully"
    )
    @GetMapping("/statistics/department")
    public DepartmentStatistics getDepartmentStatistics(
            @RequestParam String department) {

        return employeeService.getDepartmentStatistics(department);
    }
    
    @Operation(
            summary = "Get employee by ID",
            description = "Retrieves a single employee using the employee ID."
    )
    @ApiResponse(responseCode = "200", description = "Employee found")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id);
    }
    
    @Operation(
            summary = "Create a new employee",
            description = "Creates a new employee and saves the employee details in the database."
    )
    @ApiResponse(responseCode = "201", description = "Employee created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid employee data or email already exists")
    @PostMapping
    public ResponseEntity<Employee> addEmployee(
            @RequestBody @Valid Employee employee) {

        Employee savedEmployee = employeeService.addEmployee(employee);

        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }
    
    @Operation(
            summary = "Update an employee",
            description = "Updates the details of an existing employee using the employee ID."
    )
    @ApiResponse(responseCode = "200", description = "Employee updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid employee data")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @PutMapping("/{id}")
    public Employee updateEmployee(
            @PathVariable Integer id,
            @RequestBody @Valid Employee employee) {

        return employeeService.updateEmployee(id, employee);
    }
    
    @Operation(
            summary = "Delete an employee",
            description = "Deletes an existing employee using the employee ID."
    )
    @ApiResponse(responseCode = "204", description = "Employee deleted successfully")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
