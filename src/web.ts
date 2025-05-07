import { WebPlugin } from '@capacitor/core';
import type { GalleryCameraPlugin, MediaFile, CaptureOptions } from './definitions';

export class GalleryCameraWeb extends WebPlugin implements GalleryCameraPlugin {
  async getMedia(): Promise<{ files: MediaFile[] }> {
    return new Promise((resolve, reject) => {
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = 'image/*,video/*';
      input.multiple = true;

      input.onchange = () => {
        if (!input.files) return reject('No files selected');
        const files: MediaFile[] = Array.from(input.files).map(file => ({
          path: URL.createObjectURL(file),
          name: file.name,
          type: file.type
        }));
        resolve({ files });
      };

      input.click();
    });
  }

  async captureMedia(options: CaptureOptions): Promise<{ file: MediaFile }> {
    return new Promise((resolve, reject) => {
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = options.type === 'video' ? 'video/*' : 'image/*';

      input.onchange = () => {
        if (!input.files || input.files.length === 0) return reject('No file selected');
        const file = input.files[0];
        const media: MediaFile = {
          path: URL.createObjectURL(file),
          name: file.name,
          type: file.type
        };
        resolve({ file: media });
      };

      input.click();
    });
  }

  async switchCamera(): Promise<{ direction: string }> {
    console.warn('Camera switching is not supported on web.');
    return { direction: 'rear' };
  }
}