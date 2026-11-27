import { Component, computed, input } from '@angular/core';

export type IconButtonSurface = 'light' | 'dark';

const SURFACE_CLASSES: Record<IconButtonSurface, string> = {
  light:
    'text-neo-blue-dark hover:bg-neo-blue-light/60 focus-visible:ring-neo-blue focus-visible:ring-offset-white',
  dark: 'text-white/80 hover:bg-white/10 hover:text-white focus-visible:ring-white focus-visible:ring-offset-neo-blue-dark'
};

/**
 * Bouton icône rond de 40 px de la charte Néosoft. Contient une icône
 * Lucide de 20 px fournie par l'appelant, en `aria-hidden`.
 */
@Component({
  selector: 'button[app-icon-button], a[app-icon-button]',
  standalone: true,
  template: '<ng-content />',
  host: {
    '[class]': 'classes()'
  }
})
export class IconButtonComponent {
  surface = input<IconButtonSurface>('light');

  classes = computed(() => [
    'inline-flex h-10 w-10 items-center justify-center rounded-full transition-colors',
    'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2',
    SURFACE_CLASSES[this.surface()]
  ].join(' '));
}
