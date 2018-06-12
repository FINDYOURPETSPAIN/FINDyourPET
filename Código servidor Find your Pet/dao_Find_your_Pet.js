//Mónica Morán y Rubén Barrado
//DAO de conexión con la BBDD de Find your Pet

"use strict";

/**
 * Proporciona operaciones para la gestión de datos
 * en la base de datos.
 */
class DAOFindYourPet {

    /**
     * Inicializa el DAO de usuarios.
     * 
     * @param {Pool} pool Pool de conexiones MySQL. Todas las operaciones
     *                    sobre la BD se realizarán sobre este pool.
     */
    constructor(pool) {
        this.pool = pool;
    }

    /**
     * Determina si un determinado usuario aparece en la BD con la contraseña
     * pasada como parámetro.
     * 
     * @param {string} usuario email del usuario a buscar
     */
    usuarioCorrecto(usuario, callback) {

        this.pool.getConnection((err, connection) => {
            if (err) {
                callback(err);
                return;
            }
            connection.query(
                    "SELECT * " +
                    "FROM usuarios " +
                    "WHERE email = ?",
                    [usuario],
                    (err, rows) => {
                if (err) {
                    callback(err);
                    return;
                }
                connection.release();
                if (rows.length === 0) {
                    callback(null, false);
                } else {
                    callback(null, rows[0]);
                }
            });
        });
    }

    /**
     * Añade un usuario, devuelve true si todo va bien
     * 
     * @param {string} usuario usuario
     * @param {function} callback Función que recibirá el objeto error y el resultado
     */
     nuevoUsuario(usuario, callback) {

        this.pool.getConnection((err, connection) => {
            if (err) {
                callback(err);
                return;
            }
            connection.query(
                    "INSERT INTO usuarios (nombre, email, password, pregunta, respuesta) " +
                    "VALUES (?, ?, ?, ?, ?)",
                    [usuario.body.nombre, usuario.body.email, usuario.body.password, usuario.body.pregunta, usuario.body.respuesta],
                    (err, result) => {
                if (err) {
                    callback(err);
                    return;
                } else {

                    connection.release();
                    callback(null, true);
                }
            }
            );
        });
    }

    /**
     * Modifica un usuario, devuelve true si todo va bien
     * 
     * @param {string} usuario usuario
     * @param {function} callback Función que recibirá el objeto error y el resultado
     */
    modificarUsuario(usuario, callback) {

        this.pool.getConnection((err, connection) => {
            if (err) {
                callback(err);
                return;
            }
            connection.query(
                    "UPDATE usuarios SET password = ? WHERE email = ?",
                    [usuario.body.password, usuario.body.email],
                    (err, result) => {
                if (err) {
                    callback(err);
                    return;
                } else {

                    connection.release();
                    callback(null, true);
                }
            }
            );
        });
    }


    /**
     * Comprueba si existe un usuario, devuelve true o false
     * 
     * @param {string} usuario Nombre del usuario a comprobar
     * @param {function} callback Función que recibirá el objeto error y el resultado
     */
    existeUsuario(usuario, callback) {

        this.pool.getConnection((err, connection) => {
            if (err) {
                callback(err);
                return;
            }
            connection.query(
                    "SELECT * " +
                    "FROM usuarios " +
                    "WHERE login = ?",
                    [usuario],
                    (err, rows) => {
                if (err) {
                    callback(err);
                    return;
                }
                connection.release();
                if (rows.length === 0) {
                    callback(null, false);
                } else {
                    callback(null, true);
                }
            });
        });
    }

    /** ___________________________________________________________________________________________ */

     /**
     * Añade un dispositivo, devuelve true si todo va bien
     * 
     * @param {object} dispositivo 
     * @param {function} callback Función que recibirá el objeto error y el resultado
     */
    nuevoDispositivo(dispositivo, callback) {

        this.pool.getConnection((err, connection) => {
            if (err) {
                callback(err);
                return;
            }
            connection.query(
                    "INSERT INTO dispositivos (id_user, nombre, telefono, img) " +
                    "VALUES (?, ?, ?, ?)",
                    [dispositivo.params.userId, dispositivo.body.nombre, dispositivo.body.telefono, dispositivo.body.img],
                    (err, result) => {
                if (err) {
                    callback(err);
                    return;
                } else {

                    connection.release();
                    callback(null, true);
                }
            }
            );
        });
    }




    /**
     * Devuelve los dispositivos de un usuario
     * 
     * @param {string} usuario Nombre del usuario a comprobar
     * @param {function} callback Función que recibirá el objeto error y el resultado
     */
    DispositivosUser(usuario, callback) {

        this.pool.getConnection((err, connection) => {
            if (err) {
                callback(err);
                return;
            }
            connection.query(
                    "SELECT * " +
                    "FROM dispositivos " +
                    "WHERE id_user = ?",
                    [usuario],
                    (err, rows) => {
                if (err) {
                    callback(err);
                    return;
                }
                connection.release();
                if (rows.length === 0) {
                    callback(null, false);
                } else {
                    callback(null, rows);
                }
            });
        });
    }

    /**
     * Devuelve si existe el dispositivo de un usuario
     * 
     * @param {string} usuario Nombre del usuario a comprobar
     * @param {function} callback Función que recibirá el objeto error y el resultado
     */
    DispositivoExists(usuario, idDispositivo, callback) {

        this.pool.getConnection((err, connection) => {
            if (err) {
                callback(err);
                return;
            }
            connection.query(
                    "SELECT * " +
                    "FROM dispositivos " +
                    "WHERE id_user = ? AND nombre = ?",
                    [usuario, idDispositivo],
                    (err, rows) => {
                if (err) {
                    callback(err);
                    return;
                }
                connection.release();
                if (rows.length === 0) {
                    callback(null, false);
                } else {
                    callback(null, rows);
                }
            });
        });
    }


    /**
     * Elimina el dispositivo de un usuario
     * 
     * @param {string} idDispositivo id del dispositivo a eliminar
     * @param {function} callback Función que recibirá el objeto error y el resultado
     */
    DispositivoDelete(idDispositivo, callback) {

        this.pool.getConnection((err, connection) => {
            if (err) {
                callback(err);
                return;
            }
            connection.query(
                    "DELETE " +
                    "FROM dispositivos " +
                    "WHERE id = ?",
                    [idDispositivo],
                    (err) => {
                if (err) {
                    callback(false);
                    return;
                }
                else {
                    callback(null, true);
                }
            });
        });
    }


}

module.exports = {
    DAOFindYourPet: DAOFindYourPet
};