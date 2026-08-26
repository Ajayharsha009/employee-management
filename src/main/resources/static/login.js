console.log("Login page connected!");

// Get login form
const loginForm = document.getElementById("loginForm");

// Get message element
const loginMessage = document.getElementById("loginMessage");


// ==============================
// Login Form
// ==============================

loginForm.addEventListener("submit", function(event) {

    event.preventDefault();


    // Get username and password

    const username =
        document.getElementById("username").value;

    const password =
        document.getElementById("password").value;


    // ==============================
    // Send login request
    // ==============================

    fetch("/auth/login", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({

            username: username,

            password: password

        })

    })

    .then(response => {

        if (!response.ok) {

            throw new Error("Invalid username or password");

        }

        return response.json();

    })

    .then(data => {

        console.log("Login successful!");

        console.log("JWT received successfully.");


        // Store JWT

        localStorage.setItem("token", data.token);


        // Show success message

        loginMessage.textContent =
            "Login successful!";


        // Go to employee page

        window.location.href = "index.html";

    })

    .catch(error => {

        console.error("Login error:", error);

        loginMessage.textContent =
            "Invalid username or password.";

    });

});