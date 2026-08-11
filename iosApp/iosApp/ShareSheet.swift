import LinkPresentation
import UIKit

/// Presents the share sheet with a link card we control.
///
/// Without `LPLinkMetadata` iOS falls back to fetching the page itself and, until that lands, shows a
/// compass and the bare host. LPLinkMetadata carries a title, a URL and an image — there is no
/// description field, so the second line of the card is always the host.
enum ShareSheet {

    static func present(text: String, title: String, url: String) {
        guard let link = URL(string: url),
            let root = UIApplication.shared.connectedScenes
                .compactMap({ ($0 as? UIWindowScene)?.keyWindow })
                .first?.rootViewController
        else { return }

        let source = LinkItemSource(text: text, title: title, url: link)
        let controller = UIActivityViewController(activityItems: [source], applicationActivities: nil)

        if let popover = controller.popoverPresentationController {
            popover.sourceView = root.view
            popover.sourceRect = root.view.bounds
        }

        root.present(controller, animated: true)
    }
}

private final class LinkItemSource: NSObject, UIActivityItemSource {

    private let text: String
    private let title: String
    private let url: URL

    init(text: String, title: String, url: URL) {
        self.text = text
        self.title = title
        self.url = url
    }

    func activityViewControllerPlaceholderItem(_ controller: UIActivityViewController) -> Any { url }

    func activityViewController(
        _ controller: UIActivityViewController, itemForActivityType type: UIActivity.ActivityType?
    ) -> Any? {
        // Messages and Mail render the card from the URL; everything else gets the copy with the link.
        switch type {
        case .some(.message), .some(.mail): return url
        default: return "\(text)\n\n\(url.absoluteString)"
        }
    }

    func activityViewController(
        _ controller: UIActivityViewController, subjectForActivityType type: UIActivity.ActivityType?
    ) -> String {
        title
    }

    func activityViewControllerLinkMetadata(_ controller: UIActivityViewController) -> LPLinkMetadata? {
        let metadata = LPLinkMetadata()
        metadata.originalURL = url
        metadata.url = url
        metadata.title = title
        // The bare mark is white-on-transparent and disappears on the sheet's light card; ShareIcon
        // carries its own navy background, which is the design system's rule for light surfaces.
        if let icon = UIImage(named: "ShareIcon") {
            metadata.iconProvider = NSItemProvider(object: icon)
            metadata.imageProvider = NSItemProvider(object: icon)
        }
        return metadata
    }
}
