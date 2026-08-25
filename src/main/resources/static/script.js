console.log("Employee Management Frontend is connected!");


// ==============================
// Variables
// ==============================

let editingEmployeeId = null;

let allEmployees = [];

let currentPage = 1;

let employeesPerPage = 5;


// ==============================
// Get HTML Elements
// ==============================

const submitButton =
    document.getElementById("submitButton");

const cancelButton =
    document.getElementById("cancelButton");

const searchInput =
    document.getElementById("searchInput");

const message =
    document.getElementById("message");

const employeeForm =
    document.getElementById("employeeForm");

const logoutButton =
    document.getElementById("logoutButton");


// ==============================
// Get JWT Token
// ==============================

const token = localStorage.getItem("token");


// ==============================
// Protect Employee Page
// ==============================

if (!token) {

    window.location.href = "login.html";

}


// ==============================
// Get User Role from JWT
// ==============================

function getUserRole() {

    try {

        const payload =
            token.split(".")[1];

        const decodedPayload =
            JSON.parse(
                atob(
                    payload
                        .replace(/-/g, "+")
                        .replace(/_/g, "/")
                )
            );

        /*
         * Depending on how JwtService creates
         * the token, the role may be stored
         * under one of these names.
         */

        return (
            decodedPayload.role ||
            decodedPayload.roles ||
            decodedPayload.authorities ||
            ""
        );

    } catch (error) {

        console.error(
            "Could not read user role from JWT:",
            error
        );

        return "";

    }

}


const userRole = getUserRole();

const isAdmin =
    userRole === "ADMIN" ||
    userRole === "ROLE_ADMIN";

console.log("User role:", userRole);


// ==============================
// Control Admin Features
// ==============================

if (!isAdmin) {

    /*
     * USER can view employees,
     * but cannot add/update/delete.
     */

    employeeForm.style.display = "none";

    document.querySelector(".form-container").style.display = "none";


}


// ==============================
// Logout
// ==============================

logoutButton.addEventListener(
    "click",
    function() {

        localStorage.removeItem("token");

        window.location.href =
            "login.html";

    }
);


// ==============================
// Show Message
// ==============================

function showMessage(text, type) {

    message.textContent = text;

    message.className = type;

}


// ==============================
// GET - Fetch All Employees
// ==============================

function loadEmployees() {

    fetch(
        "http://localhost:8080/employees",
        {

            headers: {

                "Authorization":
                    `Bearer ${token}`

            }

        }
    )

    .then(response => {

        if (!response.ok) {

            throw new Error(
                "Failed to fetch employees: " +
                response.status
            );

        }

        return response.json();

    })

    .then(employees => {

        console.log(
            "Employees:",
            employees
        );

        allEmployees = employees;

        displayEmployees(employees);

        displayPagination(employees);

    })

    .catch(error => {

        console.error(
            "Error fetching employees:",
            error
        );

    });

}


// Load employees when page opens

loadEmployees();


// ==============================
// Display Employees
// ==============================

function displayEmployees(employees) {

    const tableBody =
        document.getElementById(
            "employeeTableBody"
        );

    tableBody.innerHTML = "";


    const startIndex =
        (currentPage - 1) *
        employeesPerPage;

    const endIndex =
        startIndex +
        employeesPerPage;


    const employeesToDisplay =
        employees.slice(
            startIndex,
            endIndex
        );


    employeesToDisplay.forEach(
        employee => {

            const row =
                document.createElement("tr");


            let actionButtons = "";


            // ADMIN can edit and delete

            if (isAdmin) {

                actionButtons = `

                    <button onclick="editEmployee(
                        ${employee.id},
                        '${employee.name}',
                        '${employee.email}',
                        '${employee.department}',
                        '${employee.salary}'
                    )">

                        Edit

                    </button>


                    <button onclick="deleteEmployee(
                        ${employee.id}
                    )">

                        Delete

                    </button>

                `;

            } else {

                actionButtons = `
                    <span>View Only</span>
                `;

            }


            row.innerHTML = `

                <td>${employee.id}</td>

                <td>${employee.name}</td>

                <td>${employee.email}</td>

                <td>${employee.department}</td>

                <td>${employee.salary}</td>

                <td>

                    ${actionButtons}

                </td>

            `;


            tableBody.appendChild(row);

        }
    );

}


// ==============================
// Pagination
// ==============================

function displayPagination(employees) {

    const pagination =
        document.getElementById(
            "pagination"
        );

    pagination.innerHTML = "";


    const totalPages =
        Math.ceil(
            employees.length /
            employeesPerPage
        );


    // No employees

    if (totalPages === 0) {

        return;

    }


    // ==============================
    // Previous Button
    // ==============================

    const previousButton =
        document.createElement(
            "button"
        );

    previousButton.textContent =
        "Previous";

    previousButton.disabled =
        currentPage === 1;


    previousButton.addEventListener(
        "click",
        function() {

            currentPage--;

            displayEmployees(
                employees
            );

            displayPagination(
                employees
            );

        }
    );


    pagination.appendChild(
        previousButton
    );


    // ==============================
    // Page Number Buttons
    // ==============================

    for (
        let page = 1;
        page <= totalPages;
        page++
    ) {

        const pageButton =
            document.createElement(
                "button"
            );

        pageButton.textContent =
            page;


        pageButton.addEventListener(
            "click",
            function() {

                currentPage =
                    page;

                displayEmployees(
                    employees
                );

                displayPagination(
                    employees
                );

            }
        );


        pagination.appendChild(
            pageButton
        );

    }


    // ==============================
    // Next Button
    // ==============================

    const nextButton =
        document.createElement(
            "button"
        );

    nextButton.textContent =
        "Next";

    nextButton.disabled =
        currentPage === totalPages;


    nextButton.addEventListener(
        "click",
        function() {

            currentPage++;

            displayEmployees(
                employees
            );

            displayPagination(
                employees
            );

        }
    );


    pagination.appendChild(
        nextButton
    );

}


// ==============================
// Search Employees
// ==============================

searchInput.addEventListener(
    "input",
    function() {

        const searchText =
            searchInput.value
                .toLowerCase()
                .trim();


        const filteredEmployees =
            allEmployees.filter(
                employee => {

                    return (

                        employee.name
                            .toLowerCase()
                            .includes(
                                searchText
                            )

                        ||

                        employee.email
                            .toLowerCase()
                            .includes(
                                searchText
                            )

                        ||

                        employee.department
                            .toLowerCase()
                            .includes(
                                searchText
                            )

                    );

                }
            );


        currentPage = 1;


        displayEmployees(
            filteredEmployees
        );

        displayPagination(
            filteredEmployees
        );

    }
);


// ==============================
// Edit Employee
// ==============================

function editEmployee(
    id,
    name,
    email,
    department,
    salary
) {

    if (!isAdmin) {

        return;

    }


    editingEmployeeId =
        id;


    document.getElementById(
        "name"
    ).value = name;

    document.getElementById(
        "email"
    ).value = email;

    document.getElementById(
        "department"
    ).value = department;

    document.getElementById(
        "salary"
    ).value = salary;


    submitButton.textContent =
        "Update Employee";


    cancelButton.style.display =
        "inline-block";

}


// ==============================
// Delete Employee
// ==============================

function deleteEmployee(id) {

    if (!isAdmin) {

        return;

    }


    const confirmed =
        confirm(
            "Are you sure you want to delete this employee?"
        );


    if (!confirmed) {

        return;

    }


    fetch(
        `http://localhost:8080/employees/${id}`,
        {

            method: "DELETE",

            headers: {

                "Authorization":
                    `Bearer ${token}`

            }

        }
    )

    .then(response => {

        if (!response.ok) {

            throw new Error(
                "Failed to delete employee: " +
                response.status
            );

        }

        return response.text();

    })

    .then(result => {

        console.log(
            "Employee deleted:",
            result
        );


        showMessage(
            "Employee deleted successfully!",
            "success"
        );


        loadEmployees();

    })

    .catch(error => {

        console.error(
            "Error deleting employee:",
            error
        );

    });

}


// ==============================
// Validate Employee
// ==============================

function validateEmployee(employee) {

    if (
        employee.name.trim() === ""
    ) {

        alert(
            "Please enter employee name."
        );

        return false;

    }


    if (
        employee.email.trim() === ""
    ) {

        alert(
            "Please enter employee email."
        );

        return false;

    }


    if (
        !employee.email.includes("@")
    ) {

        alert(
            "Please enter a valid email."
        );

        return false;

    }


    if (
        employee.department.trim() === ""
    ) {

        alert(
            "Please enter department."
        );

        return false;

    }


    if (
        employee.salary <= 0
    ) {

        alert(
            "Salary must be greater than 0."
        );

        return false;

    }


    return true;

}


// ==============================
// POST / PUT
// Add or Update Employee
// ==============================

employeeForm.addEventListener(
    "submit",
    function(event) {

        event.preventDefault();


        // Only ADMIN can submit

        if (!isAdmin) {

            return;

        }


        const employee = {

            name:
                document.getElementById(
                    "name"
                ).value,

            email:
                document.getElementById(
                    "email"
                ).value,

            department:
                document.getElementById(
                    "department"
                ).value,

            salary:
                Number(
                    document.getElementById(
                        "salary"
                    ).value
                )

        };


        // ==============================
        // Validation
        // ==============================

        if (
            !validateEmployee(
                employee
            )
        ) {

            return;

        }


        // ==============================
        // Decide POST or PUT
        // ==============================

        let url =
            "http://localhost:8080/employees";

        let method =
            "POST";


        if (
            editingEmployeeId !== null
        ) {

            url =
                `http://localhost:8080/employees/${editingEmployeeId}`;

            method =
                "PUT";

        }


        // ==============================
        // Send Request
        // ==============================

        fetch(
            url,
            {

                method: method,

                headers: {

                    "Content-Type":
                        "application/json",

                    "Authorization":
                        `Bearer ${token}`

                },

                body:
                    JSON.stringify(
                        employee
                    )

            }
        )

        .then(response => {

            if (!response.ok) {

                throw new Error(
                    "Failed to save employee: " +
                    response.status
                );

            }

            return response.json();

        })

        .then(result => {

            console.log(
                "Server response:",
                result
            );


            if (
                editingEmployeeId !==
                null
            ) {

                showMessage(
                    "Employee updated successfully!",
                    "success"
                );

            } else {

                showMessage(
                    "Employee added successfully!",
                    "success"
                );

            }


            // Reset form

            employeeForm.reset();


            editingEmployeeId =
                null;


            submitButton.textContent =
                "Add Employee";


            cancelButton.style.display =
                "none";


            // Refresh employee list

            loadEmployees();

        })

        .catch(error => {

            console.error(
                "Error saving employee:",
                error
            );


            showMessage(
                "Error saving employee.",
                "error"
            );

        });

    }
);


// ==============================
// Cancel Edit
// ==============================

cancelButton.addEventListener(
    "click",
    function() {

        employeeForm.reset();

        editingEmployeeId =
            null;

        submitButton.textContent =
            "Add Employee";

        cancelButton.style.display =
            "none";

    }
);