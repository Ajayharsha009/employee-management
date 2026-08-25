package com.ajay.employee_management.dto;

import java.math.BigDecimal;

public class DepartmentStatistics {

    private String department;
    private long employeeCount;
    private BigDecimal averageSalary;
    private BigDecimal highestSalary;
    private BigDecimal lowestSalary;

    public DepartmentStatistics(
            String department,
            long employeeCount,
            BigDecimal averageSalary,
            BigDecimal highestSalary,
            BigDecimal lowestSalary) {

        this.department = department;
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary;
        this.highestSalary = highestSalary;
        this.lowestSalary = lowestSalary;
    }

    public String getDepartment() {
        return department;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public BigDecimal getHighestSalary() {
        return highestSalary;
    }

    public BigDecimal getLowestSalary() {
        return lowestSalary;
    }
}