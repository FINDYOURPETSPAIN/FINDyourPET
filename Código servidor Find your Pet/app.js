//Mónica Morán y Rubén Barrado
//API Find your Pet implementada en NodeJS

var express = require("express"),
    app = express(),
    bodyParser  = require("body-parser"),
    methodOverride = require("method-override");
	mysql = require('mysql');

const https = require('https');
const fs = require('fs');
const daoFYP = require("./dao_Find_your_Pet.js");

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(methodOverride());

var router = express.Router();

var con = mysql.createPool({
	host: "localhost",
	user: "root",
	password: "operator_tA57",
	database: "findyourpet"
});

con.on('error', function(err) {
	console.log("[mysql error]",err);
  });


//DAOs
let daoU = new daoFYP.DAOFindYourPet(con);

/* Returns true when the item is a JSON */
function isJson(item) {
    item = typeof item !== "string"
        ? JSON.stringify(item)
        : item;

    try {
        item = JSON.parse(item);
    } catch (e) {
        return false;
    }

    if (typeof item === "object" && item !== null) {
        return true;
    }

    return false;
}


//Crea un nuevo usuario en la BBDD
app.post("/users", function(request, response) {
	 	
	daoU.nuevoUsuario(request, (err, callback) => {

		if (err) {
	
			console.log(err);
			response.end();

		} else {
	
			if (callback) {
	
				response.status(201);
				response.json({"Created": callback});
	
	
			} else {
	
				response.status(400);

			}
		}
	});
    
});

//Comprueba y devuelve el usuario existente en la BBDD
app.get("/users/:userMail", function(request, response) {
   
	var user = request.params.userMail;

    daoU.usuarioCorrecto(user, (err, callback) => {

        if (err) {

            console.log(err);
            response.end();
        } else {

            if (!callback) {

                response.json("El false no existe");

            } else {

                response.status(200);
                response.json(callback);
            }
        }
    });
});

//Comprueba y modifica el usuario en la BBDD
app.put("/users", function(request, response) {   

    daoU.modificarUsuario(request, (err, callback) => {

        if (err) {

            console.log(err);
            response.end();
        } else {

            if (!callback) {

                response.status(400);
                response.json("false");

            } else {

                response.status(200);
                response.json("true");
            }
        }
    });
});

//Comprueba y devuelve el usuario existente en la BBDD
app.get("/users/:userMail/exists", function(request, response) {
   
	var user = request.params.userMail;

    daoU.usuarioCorrecto(user, (err, callback) => {

        if (err) {

            console.log(err);
            response.end();
        } else {

            if (!callback) {

                response.json("false");

            } else {

                response.status(200);
                response.json("true");
            }
        }
    });
});

//Crea los dispositivos asociados un usuario
app.post("/users/:userId/devices", function(request, response) {
    
    var idUsuario = request.params.userId;

    daoU.nuevoDispositivo(request, (err, callback) => {

        if (err) {

            console.log(err);
            response.end();
        } else {

            if (callback) {

				response.status(201);
				response.json({"Created": callback});

            } else {

				response.status(400);

            }
        }
    });
});

//Comprueba y devuelve los dispositivos asociados a un usuario usuario
app.get("/users/:userId/devices", function(request, response) {
    
    var idUsuario = request.params.userId;

    daoU.DispositivosUser(idUsuario, (err, callback) => {

        if (err) {

            console.log(err);
            response.end();
        } else {

            if (!callback) {

                response.status(200);
                response.json("El usuario no tiene dispositivos");

            } else {

                response.status(200);
                response.json(callback);
            }
        }
    });
});


//Comprueba y devuelve si tiene dispositivos asociados un usuario 
app.get("/users/:userId/devices/:deviceId/exists", function(request, response) {
    
    var idUsuario = request.params.userId;
    var idDispositivo = request.params.deviceId;

    daoU.DispositivoExists(idUsuario, idDispositivo, (err, callback) => {

        if (err) {

            console.log(err);
            response.end();
            
        } else {

            if (!callback) {
                
                response.status(200);
                response.json("false");

            } else {

                response.status(200);
                response.json("true");
            }
        }
    });
});


//Comprueba y devuelve los dispositivos asociados a un usuario usuario
app.delete("/users/devices/:deviceId", function(request, response) {
    
    var idDispositivo = request.params.deviceId;
    
    daoU.DispositivoDelete(idDispositivo, (err, callback) => {

        if (err) {

            console.log(err);
            response.end();
            
        } else {

            if (!callback) {
                
                response.status(400);
                response.json("false");

            } else {

                response.status(200);
                response.json("true");
            }
        }
    });    
});


//Declaracion del middelware para las paginas no encontradas
app.use((request, response, next) => {
    response.status(404);
    response.end("Not found: " + request.url);
});

app.use(router);

app.listen(3000, function() {
	console.log("Node server running on http://localhost:3000");
});