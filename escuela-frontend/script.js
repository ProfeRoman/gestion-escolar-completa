const API_URL = 'http://localhost:8080/saludo';
let listaAlumnosLocal = []; // Para búsqueda instantánea sin recargar

document.addEventListener("DOMContentLoaded", () => {
    listarAlumnos();
});

// 1. OBTENER ALUMNOS (GET)
async function listarAlumnos() {
    try {
        const res = await fetch(API_URL);
        listaAlumnosLocal = await res.json();
        renderizarTarjetas(listaAlumnosLocal);
    } catch (e) {
        console.error("Error al listar:", e);
        document.getElementById('alumnosContainer').innerHTML = "<p style='color:red'>Error al conectar con el servidor.</p>";
    }
}

// 2. DIBUJAR TARJETAS (Renderizado dinámico)
function renderizarTarjetas(alumnos) {
    const contenedor = document.getElementById('alumnosContainer');
    const contador = document.getElementById('contadorComensales');
    
    if (!contenedor) return;
    contenedor.innerHTML = '';
    let comensales = 0;

    alumnos.forEach(alumno => {
        if (alumno.seQuedaAComer) comensales++;
        const tel = alumno.telefonoPadre || "";

        contenedor.innerHTML += `
            <div class="card">
                <h3>${alumno.nombre} ${alumno.apellido}</h3>
                <p><strong>DNI:</strong> ${alumno.dni}</p>
                <p><strong>ID:</strong> #${alumno.id}</p>
                
                <div class="estado-comedor" 
                     onclick="cambiarEstadoComedor(${alumno.id})" 
                     style="cursor:pointer; padding: 10px; border-radius: 8px; text-align: center; margin-top: 10px; 
                            background: ${alumno.seQuedaAComer ? 'rgba(0, 242, 254, 0.1)' : 'rgba(239, 68, 68, 0.1)'};
                            color: ${alumno.seQuedaAComer ? '#00f2fe' : '#ef4444'}; font-weight: bold; border: 1px solid ${alumno.seQuedaAComer ? '#00f2fe' : '#ef4444'};">
                    ${alumno.seQuedaAComer ? '✅ Almuerza' : '❌ No Come'}
                </div>

                <div class="card-actions">
                    <button class="btn-card btn-whatsapp" onclick="enviarWhatsapp('${tel}', '${alumno.nombre}')">Notificar Padre 💬</button>
                    <button class="btn-card btn-delete" onclick="borrarAlumno(${alumno.id})">Borrar</button>
                </div>
            </div>`;
    });

    if (contador) {
        contador.innerHTML = `Hay ${listaAlumnosLocal.length} alumnos registrados y hoy comen ${comensales} chicos.`;
    }
}

// 3. FUNCIÓN DE CHECK-IN (PUT) - ¡Esta es la que faltaba!
async function cambiarEstadoComedor(id) {
    try {
        // Llama al método cambiarComedor de tu Java
        const res = await fetch(`${API_URL}/${id}/comedor`, {
            method: 'PUT'
        });

        if (res.ok) {
            listarAlumnos(); // Refresca la lista para mostrar el cambio
        }
    } catch (e) {
        console.error("Error en check-in:", e);
    }
}

// 4. BUSCADOR (Sincronizado con onkeyup="buscar()")
function buscar() {
    const query = document.getElementById('inputBusqueda').value.toLowerCase();
    const filtrados = listaAlumnosLocal.filter(a => 
        a.nombre.toLowerCase().includes(query) || 
        a.apellido.toLowerCase().includes(query) ||
        a.dni.includes(query)
    );
    renderizarTarjetas(filtrados);
}

// 5. AGREGAR ALUMNO (POST)
async function agregarAlumno() {
    const nuevo = {
        nombre: document.getElementById('nombre').value,
        apellido: document.getElementById('apellido').value,
        dni: document.getElementById('dni').value,
        telefonoPadre: document.getElementById('telefono').value,
        seQuedaAComer: false 
    };

    if (!nuevo.nombre || !nuevo.apellido || !nuevo.dni) return alert("Faltan datos obligatorios.");

    try {
        const res = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(nuevo)
        });

        if (res.ok) {
            document.getElementById('alumnoForm').reset();
            listarAlumnos();
        }
    } catch (e) { console.error("Error al agregar:", e); }
}

// 6. BORRAR (DELETE)
async function borrarAlumno(id) {
    if (!confirm("¿Eliminar este alumno?")) return;
    try {
        await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        listarAlumnos();
    } catch (e) { console.error(e); }
}

// 7. WHATSAPP
function enviarWhatsapp(telefono, nombre) {
    if (!telefono || telefono === "null" || telefono.trim() === "") return alert("No hay teléfono.");
    const msg = `Hola! Desde la Escuela informamos que ${nombre} se queda hoy al comedor.`;
    window.open(`https://wa.me/${telefono}?text=${encodeURIComponent(msg)}`, '_blank');
}

// 8. CHATBOT
async function enviarMensaje() {
    const input = document.getElementById('user-input');
    const msgs = document.getElementById('chat-messages');
    const texto = input.value.trim();
    if (!texto) return;

    msgs.innerHTML += `<div class="msg-user">${texto}</div>`;
    input.value = "";

    try {
        if (texto.toLowerCase().includes("comedor") || texto.toLowerCase().includes("cuantos")) {
            const res = await fetch(`${API_URL}/stats`);
            const rta = await res.text();
            msgs.innerHTML += `<div class="msg-bot">${rta}</div>`;
        } else {
            msgs.innerHTML += `<div class="msg-bot">Probá preguntando "¿Cuántos comen?".</div>`;
        }
        msgs.scrollTop = msgs.scrollHeight;
    } catch (e) { console.error(e); }
}

function toggleChat() {
    document.getElementById('chat-box').classList.toggle('chat-hidden');
}