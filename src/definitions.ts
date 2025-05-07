export interface MediaFile {
  path: string;
  name: string;
  type: string;
}

export interface CaptureOptions {
  type: 'photo' | 'video';
}

export interface GalleryCameraPlugin {
  getMedia(): Promise<{ files: MediaFile[] }>;
  captureMedia(options: CaptureOptions): Promise<{ file: MediaFile }>;
  switchCamera(): Promise<{ direction: string }>;
}