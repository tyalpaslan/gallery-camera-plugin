
import Foundation
import Capacitor
import UIKit
import AVFoundation

@objc(GalleryCameraPlugin)
public class GalleryCameraPlugin: CAPPlugin, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    var call: CAPPluginCall?
    var picker: UIImagePickerController!

    @objc func captureMedia(_ call: CAPPluginCall) {
        self.call = call
        let type = call.getString("type") ?? "photo"

        DispatchQueue.main.async {
            self.picker = UIImagePickerController()
            self.picker.delegate = self
            self.picker.sourceType = .camera

            if type == "video" {
                self.picker.mediaTypes = ["public.movie"]
                self.picker.videoMaximumDuration = 30
            } else {
                self.picker.mediaTypes = ["public.image"]
            }

            self.bridge?.viewController?.present(self.picker, animated: true)
        }
    }

    public func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        call?.reject("User cancelled")
        picker.dismiss(animated: true)
    }

    public func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        picker.dismiss(animated: true)

        guard let mediaType = info[.mediaType] as? String else {
            call?.reject("No media type returned")
            return
        }

        if mediaType == "public.image" {
            guard let image = info[.originalImage] as? UIImage,
                  let imageData = image.jpegData(compressionQuality: 0.8) else {
                call?.reject("Failed to read image")
                return
            }

            let tempURL = FileManager.default.temporaryDirectory.appendingPathComponent("captured.jpg")
            try? imageData.write(to: tempURL)

            call?.resolve([
                "path": tempURL.absoluteString,
                "name": tempURL.lastPathComponent,
                "type": "image/jpeg"
            ])
        } else if mediaType == "public.movie" {
            guard let mediaURL = info[.mediaURL] as? URL else {
                call?.reject("Failed to read video")
                return
            }

            call?.resolve([
                "path": mediaURL.absoluteString,
                "name": mediaURL.lastPathComponent,
                "type": "video/mp4"
            ])
        }
    }
}
