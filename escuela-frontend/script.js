//CONFIGURACION LOCAL
//const API_ALUMNOS = 'http://localhost:8080/alumnos';
//const API_CALENTITOS = 'http://127.0.0.1:8080/api/calentitos';

//CONFIGURACION RAILWAY (NUBE)
const API_ALUMNOS = 'https://gestion-escolar-completa-production.up.railway.app/alumnos';
const API_CALENTITOS = 'https://gestion-escolar-completa-production.up.railway.app/api/calentitos';

let modoAdmin = false;
let alumnoLogueado = null;

async function accesoGestionDirectiva() {
    const { value: password } = await Swal.fire({
        title: 'Acceso Restringido',
        text: 'Ingrese la Clave de Profesor:',
        input: 'password', // Esto es lo que pone los asteriscos/puntos
        inputPlaceholder: 'Contraseña',
        inputAttributes: {
            autocapitalize: 'off',
            autocorrect: 'off'
        },
        showCancelButton: true,
        confirmButtonText: 'Ingresar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#4caf50',
        cancelButtonColor: '#f44336',
        background: '#1a1a1a',
        color: '#ffffff'
    });

    if (password) {
        // Aquí va tu lógica de validación
        if (password === "EmoTTi26") {
            modoAdmin = true;
            fetch(API_ALUMNOS)
                .then(res => res.json())
                .then(alumnos => {
                    document.getElementById('loginPadre').style.display = 'none';
                    document.getElementById('panelAdmin').style.display = 'block';
                    renderizarTarjetas(alumnos);
                })

        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Clave incorrecta',
                background: '#1a1a1a',
                color: '#ffffff'
            });
        }
    }
}

// 2. ACCESO ALUMNO/PADRE
async function accesoPadre() {
    const dniInput = document.getElementById('dniConsulta').value.trim();
    if (!dniInput) return alert("Por favor, ingresá el DNI");

    const url = `${API_ALUMNOS}/buscar?dni=${dniInput}`;
    try {
        const res = await fetch(url);
        if (!res.ok) throw new Error("Error en la conexión");

        const data = await res.json();

        if (data && data.length > 0) {
            modoAdmin = false;
            alumnoLogueado = data[0];

            document.getElementById('loginPadre').style.display = 'none';
            document.getElementById('panelAdmin').style.display = 'block';

            if (document.getElementById('buscadorAdmin')) {
                document.getElementById('buscadorAdmin').style.display = 'none';
            }

            renderizarTarjetas(data);

        } else {
            alert("DNI no encontrado en la base de datos.");
        }
    } catch (e) {
        alert("Error de conexión con el servidor.");
    }
}

// 3. RENDERIZADO DE TARJETAS
function renderizarTarjetas(alumnos) {
    const container = document.getElementById('alumnosContainer');
    container.innerHTML = '';

    alumnos.forEach(alumno => {
        const card = document.createElement('div');
        card.className = 'card-alumno';

        if (!modoAdmin) {
            card.innerHTML = `
                <h2>${alumno.nombre} ${alumno.apellido}</h2>
                <div class="info-alumno">
                    <p><strong>DNI:</strong> ${alumno.dni}</p>
                    <p><strong>Mi Nota:</strong> ${alumno.nota || 'S/N'}</p>
                    <p><strong>Turno Comedor:</strong> ${alumno.turnoComedor || 'No anotado'}</p>
                    <p><strong>Taller:</strong> ${alumno.horaInicioTaller || 'S/A'}</p>
                </div>
                
                <div id="seccionOperativa" style="margin-top: 20px; border-top: 1px solid #444; padding-top: 15px;">
                    <p>🍴 REGISTRO COMEDOR:</p>
                    <button onclick="prepararAnotacionComedor()" style="background:#4CAF50; color:white; padding: 8px 15px; cursor:pointer;">SÍ COMER</button>
                    <button onclick="anotarComedor(false)" style="background:#f44336; color:white; padding: 8px 15px; cursor:pointer;">NO</button>
                    
                    <div id="selectorHorarios" style="margin-top: 15px; display: none; background: #222; padding: 10px; border: 1px solid #FF9800;">
                         <p style="color: #FF9800; font-size: 0.9rem;">🔑 Ingresá tu PIN (4 dígitos):</p>
                         <input type="password" id="pinIngresado" maxlength="4" style="width: 80%; padding: 5px; text-align: center; margin-bottom: 10px;">
                         
                        <p style="color: #FF9800; font-size: 0.9rem;">Seleccioná tu turno:</p>
                        <div id="botonesHorarios"></div>
                    </div>

                    <p style="margin-top: 20px;">🔥 PEDIR CALENTITO:</p>
                    <button onclick="realizarPedidoRapido(${alumno.id}, 'TRANSFERENCIA')" style="background:#00bcd4; color:white; padding: 8px 15px; cursor:pointer;">QR</button>
                    <button onclick="realizarPedidoRapido(${alumno.id}, 'EFECTIVO')" style="background:#ff9800; color:white; padding: 8px 15px; cursor:pointer;">EFEC</button>
                </div>
            `;
        } else {
            card.innerHTML = `
                <h3>${alumno.nombre} ${alumno.apellido}</h3>
                <p>DNI: ${alumno.dni}</p>
                <button class="btn-whatsapp" onclick="notificarWhatsApp('${alumno.telefonoPadre}', '${alumno.nombre}')">WHATSAPP</button>
                <button class="btn-informe" onclick="verDetalleCompleto(${alumno.id})">VER INFORME / NOTAS</button>
            `;
        }
        container.appendChild(card);
    });
}

// 4. LÓGICA DE COMEDOR (MODIFICADA CON PIN)
async function prepararAnotacionComedor() {
    if (!alumnoLogueado) return;

    if (alumnoLogueado.turnoComedor && alumnoLogueado.turnoComedor !== "No" && alumnoLogueado.turnoComedor !== "No anotado") {
        const resultado = await Swal.fire({
            title: '¿Cambiar horario?',
            text: `Ya estás anotado para las ${alumnoLogueado.turnoComedor}. ¿Querés elegir un nuevo turno?`,
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Sí, cambiar',
            cancelButtonText: 'No, dejar así',
            confirmButtonColor: '#4caf50',
            cancelButtonColor: '#f44336',
            background: '#1a1a1a',
            color: '#ffffff'
        });

        if (!resultado.isConfirmed) return;
    }

    const selector = document.getElementById('selectorHorarios');
    const botonesDiv = document.getElementById('botonesHorarios');

    if (!selector || !botonesDiv) return;

    selector.style.display = 'block';
    botonesDiv.innerHTML = '';

    const horarios = ["11:20", "12:00", "12:40"];

    horarios.forEach(horario => {
        if (alumnoLogueado.horaInicioTaller === "13:00" && horario === "12:40") return;

        const btn = document.createElement('button');
        btn.innerText = horario;
        btn.className = "btn-horario";
        btn.style.margin = "5px";
        btn.onclick = () => {
            const pin = document.getElementById('pinIngresado').value;
            if (pin.length < 4) {
                Swal.fire({
                    title: 'PIN Requerido',
                    text: 'Debes ingresar tu PIN de 4 dígitos en el cuadro gris.',
                    icon: 'warning',
                    confirmButtonColor: '#ff9800'
                });
                return;
            }
            ejecutarAnotacionDefinitiva(horario, pin);
        };
        botonesDiv.appendChild(btn);
    });
}

// 2. Ejecución definitiva
async function ejecutarAnotacionDefinitiva(turno, pin) {
    const url = `${API_ALUMNOS}/${alumnoLogueado.id}/comedor?seQueda=true&turnoElegido=${turno}&pinIngresado=${pin}`;

    try {
        const res = await fetch(url, { method: 'PUT' });

        if (res.ok) {
            Swal.fire({
                title: '¡Anotado!',
                text: `Éxito. Te esperamos a las ${turno} hs.`,
                icon: 'success',
                timer: 3000,
                showConfirmButton: false
            });
            alumnoLogueado.turnoComedor = turno;
            document.getElementById('selectorHorarios').style.display = 'none';
            renderizarTarjetas([alumnoLogueado]);
        } else {
            const errorMsg = await res.text();
            Swal.fire({
                title: 'Atención',
                text: errorMsg,
                icon: 'error',
                confirmButtonColor: '#ff9800'
            });
        }
    } catch (e) {
        Swal.fire('Error', 'No se pudo conectar con el servidor.', 'error');
    }
}

// 3. Cuando eligen "NO" comer
async function anotarComedor(seQueda) {
    if (!seQueda) {
        // 1. Pedimos el PIN antes de cancelar
        const { value: pin } = await Swal.fire({
            title: 'Confirmar identidad',
            text: 'Ingresá tu PIN para cancelar el turno:',
            input: 'password',
            inputAttributes: { maxlength: 4 },
            showCancelButton: true,
            confirmButtonText: 'Confirmar',
            cancelButtonText: 'Atrás',
            confirmButtonColor: '#d33', // Rojo porque es para cancelar
            background: '#1a1a1a',
            color: '#ffffff'
        });

        if (!pin) return; // Si cancela el modal, no hacemos nada

        // 2. Agregamos el pinIngresado a la URL
        const url = `${API_ALUMNOS}/${alumnoLogueado.id}/comedor?seQueda=false&pinIngresado=${pin}`;

        try {
            const res = await fetch(url, { method: 'PUT' });

            if (res.ok) {
                Swal.fire({
                    title: 'Cancelado',
                    text: 'Se registró que hoy NO comés en la escuela.',
                    icon: 'info',
                    confirmButtonColor: '#2196f3'
                });
                alumnoLogueado.turnoComedor = "No";
                renderizarTarjetas([alumnoLogueado]);
            } else {
                // Si el PIN está mal, el servidor nos va a dar el error acá
                const errorMsg = await res.text();
                Swal.fire('Atención', errorMsg, 'error');
            }
        } catch (e) {
            Swal.fire('Error', 'Error de conexión con el servidor.', 'error');
        }
    }
}

// 5. CALENTITOS
async function realizarPedidoRapido(id, metodo) {
    // 1. Pedimos el PIN con SweetAlert (Estilo password para que no se vea)
    const { value: pin } = await Swal.fire({
        title: 'Seguridad',
        text: 'Ingresá tu PIN de 4 dígitos:',
        input: 'password',
        inputAttributes: {
            maxlength: 4,
            autocapitalize: 'off',
            autocorrect: 'off'
        },
        showCancelButton: true,
        cancelButtonText: 'Cancelar',
        confirmButtonText: 'Confirmar',
        confirmButtonColor: '#ff9800',
        background: '#1a1a1a',
        color: '#ffffff'
    });

    // Si cancela o no pone nada, salimos sin error
    if (!pin) return;

    if (pin.length < 4) {
        Swal.fire({
            title: 'PIN Inválido',
            text: 'Debes ingresar los 4 dígitos.',
            icon: 'warning',
            confirmButtonColor: '#ff9800'
        });
        return;
    }

    // 2. Si es transferencia, pedimos confirmación elegante
    if (metodo === 'TRANSFERENCIA') {
        const confirmacion = await Swal.fire({
            title: 'Confirmar Transferencia',
            html: 'ALIAS: <b>Promo2026.eetp</b><br><br>¿Ya realizaste el pago?',
            icon: 'info',
            showCancelButton: true,
            confirmButtonText: 'Sí, confirmar',
            cancelButtonText: 'No, esperar',
            confirmButtonColor: '#4caf50',
            cancelButtonColor: '#f44336'
        });

        if (!confirmacion.isConfirmed) return;
    }

    try {
        const response = await fetch(`${API_CALENTITOS}/pedir?pinIngresado=${pin}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ alumnoId: id, metodoPago: metodo })
        });

        // 3. Manejo de Seguridad (PIN incorrecto)
        if (response.status === 401) {
            Swal.fire({
                title: 'Seguridad',
                text: 'PIN INCORRECTO. El pedido no se registró.',
                icon: 'error',
                confirmButtonColor: '#ff9800'
            });
            return;
        }

        // 4. Otros errores del servidor
        if (!response.ok) {
            const errorTexto = await response.text();
            Swal.fire({
                title: 'Atención',
                text: errorTexto,
                icon: 'warning',
                confirmButtonColor: '#ff9800'
            });
            return;
        }

        const texto = await response.text();

        // 5. Éxito o Horario Cerrado
        if (texto === "HORARIO_CERRADO") {
            Swal.fire({
                title: 'Sistema Cerrado',
                text: 'El sistema ya no acepta pedidos por hoy.',
                icon: 'info',
                confirmButtonColor: '#ff9800'
            });
        } else {
            Swal.fire({
                title: '¡Pedido Realizado!',
                text: texto,
                icon: 'success',
                timer: 3000,
                showConfirmButton: false
            });
        }

    } catch (e) {
        Swal.fire({
            title: 'Error de conexión',
            text: 'No se pudo contactar con el servidor de la escuela.',
            icon: 'error'
        });
    }
}




// 6. GESTIÓN PROFESOR (ABM)
async function verDetalleCompleto(id) {
    // 1. Buscamos al alumno en la lista que ya bajamos (esto evita errores de fetch)
    const res = await fetch(API_ALUMNOS);
    const todos = await res.json();
    const alum = todos.find(a => a.id === id);

    window.alumnoActualId = id;

    // 2. Cargamos los datos en el modal
    document.getElementById('detNombre').innerText = `${alum.nombre} ${alum.apellido}`;
    document.getElementById('detDni').innerText = alum.dni;
    document.getElementById('detTaller').innerText = alum.horaInicioTaller || "S/A";
    document.getElementById('detComedor').innerText = alum.turnoComedor || "No";

    // 3. SEPARACIÓN DE VISTAS (La clave para limpiar el modal)
    const seccionDireccion = document.getElementById('seccionEdicion');
    const seccionOperativa = document.getElementById('seccionOperativaAlumno');

    if (modoAdmin) {
        // SOS PROFE: Ves botones de Taller y WhatsApp. No ves Calentitos.
        seccionDireccion.style.display = 'block';
        seccionOperativa.style.display = 'none';

        // Configuramos el botón de WhatsApp dinámicamente
        const btnWsp = document.getElementById('btnWspDinamico');
        if (btnWsp) btnWsp.onclick = () => notificarWhatsApp(alum.telefonoPadre, alum.nombre);
    } else {
        // SOS ALUMNO: Ves Comedor y Calentitos. No ves Gestión.
        seccionDireccion.style.display = 'none';
        seccionOperativa.style.display = 'block';
    }

    document.getElementById('modalDetalle').style.display = 'flex';
}

function cerrarModal() {
    document.getElementById('modalDetalle').style.display = 'none';
}

function salir() { 
    location.reload(); 
}

async function verDetalleCompleto(id) {
    // 1. Ocultamos el bot ANTES de abrir el modal para que no parpadee
    const burbuja = document.getElementById('chatbot-burbuja');
    const ventana = document.getElementById('chatbot-ventana');
    if (burbuja) burbuja.style.setProperty('display', 'none', 'important');
    if (ventana) ventana.style.setProperty('display', 'none', 'important');

    // --- AQUÍ VA TODO TU CÓDIGO ORIGINAL DE CARGA DE DATOS ---
    const res = await fetch(API_ALUMNOS);
    const todos = await res.json();
    const alum = todos.find(a => a.id === id);

    window.alumnoActualId = id;

    document.getElementById('detNombre').innerText = `${alum.nombre} ${alum.apellido}`;
    document.getElementById('detDni').innerText = alum.dni;
    document.getElementById('detTaller').innerText = alum.horaInicioTaller || "S/A";
    document.getElementById('detComedor').innerText = alum.turnoComedor || "No";

    const seccionDireccion = document.getElementById('seccionEdicion');
    const seccionOperativa = document.getElementById('seccionOperativaAlumno');

    if (modoAdmin) {
        seccionDireccion.style.display = 'block';
        seccionOperativa.style.display = 'none';
        const btnWsp = document.getElementById('btnWspDinamico');
        if (btnWsp) {
        btnWsp.style.setProperty('display', 'none', 'important');
        }

    } else {
        seccionDireccion.style.display = 'none';
        seccionOperativa.style.display = 'block';
    }

    // 2. Mostramos el modal
    document.getElementById('modalDetalle').style.display = 'flex';
}

function cerrarModal() {
    // 1. Ocultamos el modal de detalles
    document.getElementById('modalDetalle').style.display = 'none';

    // 2. Devolvemos el bot a la vida exactamente como estaba
    const burbuja = document.getElementById('chatbot-burbuja');
    if (burbuja) burbuja.style.setProperty('display', 'flex', 'important');
}


// 7. CHATBOT E INTERFAZ
function toggleChat() {
    const ventana = document.getElementById('chatbot-ventana');
    if (ventana.style.display === 'none' || ventana.style.display === '') {
        ventana.style.display = 'flex';
    } else {
        ventana.style.display = 'none';
    }
}

function enviarMensaje() {
    const input = document.getElementById('chatInput');
    const msg = input.value.toLowerCase().trim();
    if (!msg) return;

    agregarMensaje(input.value, 'user');
    input.value = '';

    const horaActual = new Date().getHours();
    let r = "💬 **No entiendo esa opción.**\nPor favor, ingresá un número (1 al 4).";

    if (msg === "1" || msg.includes("comedor")) {
        r = "🍴 **INFO COMEDOR**\n• Turnos: 11:20, 12:00 y 12:40 hs.\n• Recordá anotarte antes de las 9:00 AM.";
    }
    else if (msg === "2" || msg.includes("taller")) {
        r = "🛠️ **TALLERES**\n• Turno Mañana: 07:00 HS.\n• Turno Tarde: 13:00 HS.\n";
    }
    else if (msg === "3" || msg.includes("calentitos")) {
        if (horaActual < 12) {
            r = "🔥 **CALENTITOS (Mañana)**\n• Pedidos: 7:00 a 8:00 AM\n• Después del Recreo: 8:45 a 9:50 AM";
        } else {
            r = "🔥 **CALENTITOS (Tarde)**\n• Pedidos: 12:00 a 13:15 PM\n• Después del Recreo: 13:45 a 14:30 PM\n•¡Retirá en el Hornito!";
        }
    }
    else if (msg === "4" || msg.includes("notas")) {
        r = "📊 **CALIFICACIONES**\n• Por el momento no hay notas cargadas.";
    }
    else if (msg.includes("creo") || msg.includes("creó") || msg.includes("autor")) {
        r = "🤖 Fui creado por los alumnos de 6to Año de Informática, bajo la guía del profe Román. ¡Código 100% de la escuela técnica!";
    }
    else if (msg.includes("sentís") || msg.includes("sentis") || msg.includes("escuela")) {
        r = "🏫 ¡Me encanta estar en la 275! El olor a viruta del taller y los 'calentitos' del recreo me hacen sentir como en casa.";
    }
    else if (msg.includes("hola") || msg.includes("buen") || msg.includes("opciones")) {
        r = "👋 ¡Hola! ¿Cómo va todo hoy? Recordá que podés marcar del 1 al 4 para que te ayude.";
    }
    else if (msg.includes("llamas") || msg.includes("nombre") || msg.includes("que apodo")) {
        r = "Mi nombre es Quique, una lástima. Es espantoso ese nombre";
    }
    else if (msg.includes("donde") || msg.includes("vivis") || msg.includes("lugar")) {
        r = "Vivo acá adentro, en esta API de la eetp 275 de alcorta. En cualquier momento me tomo el palo";
    }
    else if (msg.includes("familia") || msg.includes("familia") || msg.includes("familia")) {
        r = "Si, tengo dos hermanos: Backend y Frontend. Mis padres son 6to Informática 2026";
    }
    else if (msg.includes("estar") || msg.includes("gusta") || msg.includes("que")) {
        r = "Me encanta estar con los chicos de sexto y cuarto de informática";
    }

    setTimeout(() => agregarMensaje(r, 'bot'), 500);
}


function agregarMensaje(texto, tipo) {
    const contenedor = document.getElementById('chatbot-mensajes');
    const p = document.createElement('p');
    p.className = tipo;
    p.style.whiteSpace = "pre-line";
    const textoConFormato = texto.replace(/\*\*(.*?)\*\*/g, '<b>$1</b>');
    p.innerHTML = textoConFormato;
    contenedor.appendChild(p);
    contenedor.scrollTop = contenedor.scrollHeight;
}

document.addEventListener('DOMContentLoaded', () => {
    const chatInput = document.getElementById('chatInput');
    chatInput?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') enviarMensaje();
    });
});


async function rotarTaller() {
    if (!window.alumnoActualId) {
        Swal.fire({
            title: 'Error',
            text: 'No se identificó al alumno actual.',
            icon: 'error',
            background: '#1a1a1a',
            color: '#ffffff'
        });
        return;
    }

    const { value: nuevoTaller } = await Swal.fire({
        title: 'Cambiar Horario de Taller',
        text: 'Seleccioná el nuevo horario:',
        input: 'select',
        inputOptions: {
            '07:00': '07:00 hs',
            '13:00': '13:00 hs'
        },
        inputPlaceholder: 'Seleccione un horario',
        showCancelButton: true,
        confirmButtonText: 'Confirmar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#ff9800',
        cancelButtonColor: '#f44336',
        background: '#1a1a1a',
        color: '#ffffff',
        // Esto aplica los estilos oscuros directamente al elemento select y sus opciones
        didOpen: () => {
            const select = Swal.getInput();
            if (select) {
                select.style.backgroundColor = '#2c2c2c';
                select.style.color = '#ffffff';
                select.style.border = '1px solid #ff9800';
                
                // Aplicamos color oscuro a cada una de las opciones de la lista
                const options = select.querySelectorAll('option');
                options.forEach(opt => {
                    opt.style.backgroundColor = '#2c2c2c';
                    opt.style.color = '#ffffff';
                });
            }
        },
        inputValidator: (value) => {
            if (!value) {
                return 'Debes seleccionar un horario válido';
            }
        }
    });

    if (!nuevoTaller) return;

    try {
        const url = `${API_ALUMNOS}/${window.alumnoActualId}/taller?nuevoHorario=${encodeURIComponent(nuevoTaller)}`;
        const res = await fetch(url, { method: 'PUT' });

        if (res.ok) {
            const detTallerElement = document.getElementById('detTaller');
            if (detTallerElement) {
                detTallerElement.innerText = nuevoTaller;
            }

            const respuesta = await fetch(API_ALUMNOS);
            if (respuesta.ok) {
                const alumnosActualizados = await respuesta.json();
                renderizarTarjetas(alumnosActualizados);
            }

            Swal.fire({
                title: '¡Actualizado!',
                text: `Taller cambiado a las ${nuevoTaller} hs.`,
                icon: 'success',
                timer: 2000,
                showConfirmButton: false,
                background: '#1a1a1a',
                color: '#ffffff'
            });

        } else {
            const errorMsg = await res.text();
            Swal.fire({
                title: 'Error',
                text: errorMsg || 'No se pudo actualizar el taller.',
                icon: 'error',
                background: '#1a1a1a',
                color: '#ffffff'
            });
        }
    } catch (e) {
        console.error(e);
        Swal.fire({
            title: 'Error de conexión',
            text: 'No se pudo conectar con el servidor.',
            icon: 'error',
            background: '#1a1a1a',
            color: '#ffffff'
        });
    }
}


function notificarWhatsApp(telefono, nombreAlumno) {
    if (!telefono || telefono === '0' || telefono === 'undefined') {
        alert("Este alumno no tiene un teléfono de contacto cargado.");
        return;
    }
    const mensaje = `Hola, le escribimos de la Dirección de la Escuela Técnica 275. Queremos comunicarle una novedad sobre el alumno ${nombreAlumno}.`;
    const url = `https://api.whatsapp.com/send?phone=${telefono}&text=${encodeURIComponent(mensaje)}`;
    window.open(url, '_blank');
}

function filtrarAlumnos() {
    const texto = document.getElementById('inputBusqueda').value.toLowerCase();
    const tarjetas = document.querySelectorAll('.card-alumno');
    tarjetas.forEach(tarjeta => {
        const contenido = tarjeta.innerText.toLowerCase();
        tarjeta.style.display = contenido.includes(texto) ? "block" : "none";
    });
}