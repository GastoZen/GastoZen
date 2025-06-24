class SessionManager {
    constructor() {
        this.SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutos
        this.checkInterval = null;
        this.init();
    }

    init() {
        this.startSessionCheck();
        this.setupActivityListeners();
    }

    startSessionCheck() {
        this.checkInterval = setInterval(() => {
            this.checkSessionExpiry();
        }, 60000); // Verifica a cada minuto
    }

    setupActivityListeners() {
        const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
        events.forEach(event => {
            document.addEventListener(event, () => {
                this.updateLastActivity();
            }, true);
        });
    }

    updateLastActivity() {
        localStorage.setItem('lastActivity', Date.now().toString());
    }

    checkSessionExpiry() {
        const token = localStorage.getItem('token');
        const lastActivity = localStorage.getItem('lastActivity');
        
        if (!token || !lastActivity) {
            this.logout();
            return;
        }

        const timeElapsed = Date.now() - parseInt(lastActivity);
        
        if (timeElapsed > this.SESSION_TIMEOUT) {
            alert('Sua sessão expirou por inatividade. Você será redirecionado para o login.');
            this.logout();
        }
    }

    async logout() {
        const token = localStorage.getItem('token');
        
        // Chama logout no backend
        if (token) {
            try {
                await fetch('/api/auth/logout', {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer ' + token
                    }
                });
            } catch (error) {
                console.error('Erro no logout backend:', error);
            }
        }
        
        // Limpa dados locais
        localStorage.removeItem('token');
        localStorage.removeItem('loginTime');
        localStorage.removeItem('lastActivity');
        
        // Para o timer
        if (this.checkInterval) {
            clearInterval(this.checkInterval);
        }

        // Desloga do Firebase
        if (firebase && firebase.auth) {
            firebase.auth().signOut().catch(console.error);
        }

        // Redireciona para login
        window.location.href = '/login.html';
    }

    isSessionValid() {
        const token = localStorage.getItem('token');
        const lastActivity = localStorage.getItem('lastActivity');
        
        if (!token || !lastActivity) {
            return false;
        }

        const timeElapsed = Date.now() - parseInt(lastActivity);
        return timeElapsed <= this.SESSION_TIMEOUT;
    }
}

// Instância global
const sessionManager = new SessionManager();