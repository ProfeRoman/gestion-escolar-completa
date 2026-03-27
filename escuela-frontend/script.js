const API_URL = "http://127.0.0.1:8080/saludo";
let idEdicion = null;

// 1. FUNCIÓN PRINCIPAL: Cargar la lista completa al iniciar
async function listarAlumnos() {
    console.log("Cargando Dashboard...");
    try {
        const res = await fetch(API_URL);
        const alumnos = await res.json();
        renderizarTarjetas(alumnos);
    } catch (e) {
        console.error("Error al listar alumnos:", e);
    }
}

// 2. EL "MOLDE": Esta función dibuja las tarjetas en el HTML
function renderizarTarjetas(lista) {
    const container = document.getElementById('alumnosContainer');
    container.innerHTML = ''; // Limpiamos el contenedor

    lista.forEach((alumno) => {
        const comedorStatus = alumno.seQuedaAComer ? "✅ Almuerza" : "❌ No Come";
        
        container.innerHTML += `
            <div class="card">
                <h3>${alumno.nombre} ${alumno.apellido}</h3>
                <p><strong>DNI:</strong> ${alumno.dni}</p>
                <p><strong>ID:</strong> #${alumno.id}</p>
                
                <div class="card-actions">
                    <button class="btn-card btn-comedor" onclick="cambiarComida(${alumno.id})">
                        ${comedorStatus}
                    </button>
                </div>

                <div class="card-actions">
                    <button class="btn-card btn-edit" onclick="prepararEdicion(${alumno.id}, '${alumno.nombre}', '${alumno.apellido}', '${alumno.dni}')">
                        Editar
                    </button>
                    <button class="btn-card btn-delete" onclick="borrarAlumno(${alumno.id})">
                        Borrar
                    </button>
                </div>
            </div>`;
    });

    // Actualizamos el contador de comensales con la lista actual
    actualizarContador(lista);
}

// 3. BUSCADOR EN VIVO: Se activa con onkeyup en el HTML
async function buscar() {
    const term = document.getElementById('inputBusqueda').value.trim();
    
    // Si borrás el buscador, volvemos a la lista completa
    if (term === "") {
        listarAlumnos();
        return;
    }

    try {
        // Llamamos a la ruta de búsqueda de tu Java
        const res = await fetch(`${API_URL}/buscar?nombre=${term}`);
        const filtrados = await res.json();
        
        // Dibujamos solo los que coincidieron
        renderizarTarjetas(filtrados);
    } catch (e) {
        console.error("Error en la búsqueda:", e);
    }
}

// 4. CONTADOR: Suma los "Sí" de la lista que esté en pantalla
function actualizarContador(lista) {
    const total = lista.filter(a => a.seQuedaAComer === true).length;
    const cuadroTotal = document.getElementById('contadorComensales');
    if (cuadroTotal) {
        cuadroTotal.innerHTML = `Total para el comedor: <strong>${total}</strong>`;
    }
}

// 5. ACCIONES: Comedor, Borrar y Editar
async function cambiarComida(id) {
    await fetch(`${API_URL}/${id}/comedor`, { method: 'PUT' });
    listarAlumnos();
}

async function borrarAlumno(id) {
    if (confirm("¿Eliminar alumno?")) {
        await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        listarAlumnos();
    }
}

function prepararEdicion(id, nombre, apellido, dni) {
    document.getElementById('nombre').value = nombre;
    document.getElementById('apellido').value = apellido;
    document.getElementById('dni').value = dni;
    idEdicion = id;
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// 6. FORMULARIO: Guardar o Actualizar
document.getElementById('alumnoForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alumno = {
        nombre: document.getElementById('nombre').value,
        apellido: document.getElementById('apellido').value,
        dni: document.getElementById('dni').value
    };

    const url = idEdicion ? `${API_URL}/${idEdicion}` : API_URL;
    const metodo = idEdicion ? 'PUT' : 'POST';

    await fetch(url, {
        method: metodo,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(alumno)
    });

    idEdicion = null;
    document.getElementById('alumnoForm').reset();
    listarAlumnos();
});

// Arrancamos!
window.onload = listarAlumnos;