import { Injectable } from '@angular/core'; // Imports Injectable decorator from Angular core for dependency injection.

@Injectable({ providedIn: 'root' }) // Decorator to make the class injectable as a service at root level.
export class ToastService { // Main service class for displaying toast notifications.
  private containerId = 'app-toast-container'; // ID for the toast container element.

  // Private method to ensure the toast container exists in the DOM.
  private ensureContainer(): HTMLElement {
    let c = document.getElementById(this.containerId) as HTMLElement | null; // Gets the container element.
    if (!c) { // If container doesn't exist.
      c = document.createElement('div'); // Creates a new div element.
      c.id = this.containerId; // Sets the ID.
      document.body.appendChild(c); // Appends to body.
    }
    return c; // Returns the container.
  }

  // Method to show a toast notification.
  show(message: string, type: 'success' | 'error' | 'info' = 'info', duration = 3000) {
    try {
      const container = this.ensureContainer(); // Ensures container exists.
      const el = document.createElement('div'); // Creates toast element.
      el.className = `toast toast-${type}`; // Sets CSS classes.
      el.textContent = message; // Sets message text.
      container.appendChild(el); // Appends to container.
      setTimeout(() => { // After duration.
        el.classList.add('toast-hide'); // Adds hide class.
        setTimeout(() => el.remove(), 300); // Removes element after animation.
      }, duration);
    } catch (e) {
      console.warn('Toast show failed', e); // Logs warning on error.
    }
  }
}
