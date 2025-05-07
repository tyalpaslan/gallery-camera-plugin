import { registerPlugin } from '@capacitor/core';
import type { GalleryCameraPlugin } from './definitions';

const GalleryCamera = registerPlugin<GalleryCameraPlugin>('GalleryCamera', {
  web: () => import('./web').then(m => new m.GalleryCameraWeb()),
});

export * from './definitions';
export { GalleryCamera };