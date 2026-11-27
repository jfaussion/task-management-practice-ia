import { Component, booleanAttribute, computed, input } from '@angular/core';

export type PillButtonVariant = 'primary' | 'secondary' | 'on-dark';
export type PillButtonSize = 'md' | 'sm';

const VARIANT_CLASSES: Record<PillButtonVariant, string> = {
  primary:
    'border border-neo-blue bg-neo-blue text-white hover:border-neo-blue-dark hover:bg-neo-blue-dark focus-visible:ring-neo-blue focus-visible:ring-offset-white',
  secondary:
    'border border-neo-blue-dark bg-transparent text-neo-blue-dark hover:bg-neo-blue-dark hover:text-white focus-visible:ring-neo-blue focus-visible:ring-offset-white',
  'on-dark':
    'border border-white bg-transparent text-white hover:bg-white hover:text-neo-blue-dark focus-visible:ring-white focus-visible:ring-offset-neo-blue-dark'
};

const SIZE_CLASSES: Record<PillButtonSize, string> = {
  md: 'px-[26px] py-3 text-base leading-5',
  sm: 'h-9 min-w-9 px-3 text-sm leading-none'
};

/**
 * Bouton pilule de la charte Néosoft, unique point d'entrée pour tous les
 * boutons texte. Aucune logique métier : l'appelant garde ses handlers.
 */
@Component({
  selector: 'button[app-pill-button], a[app-pill-button]',
  standalone: true,
  template: '<ng-content />',
  host: {
    '[class]': 'classes()',
    '[attr.disabled]': 'disabled() ? "" : null',
    '[attr.aria-disabled]': 'disabled() ? "true" : null'
  }
})
export class PillButtonComponent {
  variant = input<PillButtonVariant>('secondary');
  size = input<PillButtonSize>('md');
  disabled = input(false, { transform: booleanAttribute });

  classes = computed(() => [
    'inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-full font-medium transition-colors',
    'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2',
    'disabled:pointer-events-none disabled:opacity-40',
    VARIANT_CLASSES[this.variant()],
    SIZE_CLASSES[this.size()]
  ].join(' '));
}
