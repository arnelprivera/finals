package TiwalApp.ui.panels;

import TiwalApp.TiwalApp;
import TiwalApp.ui.components.UIHelper;
import TiwalApp.ui.components.ModernButton;
import TiwalApp.ui.components.GlassPanel;
import TiwalApp.utils.Constants;
import TiwalApp.database.AnnouncementDAO;
import TiwalApp.models.Announcement;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class UserAnnouncementsPanel extends GlassPanel {
    private TiwalApp app;
    private JPanel announcementsPanel;
    private AnnouncementDAO announcementDAO;

    public UserAnnouncementsPanel(TiwalApp app) {
        this.app = app;
        this.announcementDAO = new AnnouncementDAO(app.getDatabaseConnection());
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // Header Panel
        JPanel headerPanel = createCardPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel leftHeaderPanel = new JPanel(new BorderLayout());
        leftHeaderPanel.setOpaque(false);

        JLabel titleLabel = UIHelper.createModernLabel("Announcements",
                Constants.HEADING_FONT, Constants.TEXT_PRIMARY);
        leftHeaderPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(Constants.PRIMARY_COLOR);
        leftHeaderPanel.add(timeLabel, BorderLayout.SOUTH);

        ModernButton backBtn = new ModernButton("← Back to Dashboard", Constants.PRIMARY_COLOR);
        backBtn.addActionListener(e -> app.showUserDashboard());
        backBtn.setFont(Constants.BUTTON_FONT);

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);

        // Announcements List
        announcementsPanel = new JPanel();
        announcementsPanel.setLayout(new BoxLayout(announcementsPanel, BoxLayout.Y_AXIS));
        announcementsPanel.setOpaque(false);
        announcementsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(announcementsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshAnnouncements() {
        announcementsPanel.removeAll();

        List<Announcement> announcements = announcementDAO.getActiveAnnouncements();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Announcement announcement : announcements) {
            JPanel announcementCard = createAnnouncementCard(announcement, sdf);
            announcementsPanel.add(announcementCard);
            announcementsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        if (announcements.isEmpty()) {
            JLabel noAnnouncementsLabel = new JLabel("No announcements available.");
            noAnnouncementsLabel.setFont(Constants.NORMAL_FONT);
            noAnnouncementsLabel.setForeground(Constants.TEXT_SECONDARY);
            noAnnouncementsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            announcementsPanel.add(noAnnouncementsLabel);
        }

        announcementsPanel.revalidate();
        announcementsPanel.repaint();
    }

    private JPanel createAnnouncementCard(Announcement announcement, SimpleDateFormat sdf) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color glassColor = new Color(255, 255, 255, 200);
                g2d.setColor(Constants.SHADOW_COLOR);
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 15, 15);

                g2d.setColor(glassColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2d.dispose();
            }
        };

        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title with category badge
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(announcement.getTitle());
        titleLabel.setFont(Constants.SUBHEADING_FONT);
        titleLabel.setForeground(Constants.PRIMARY_COLOR);

        JLabel categoryLabel = new JLabel(announcement.getCategory());
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setBackground(new Color(103, 58, 183, 150));
        categoryLabel.setOpaque(true);
        categoryLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(categoryLabel, BorderLayout.EAST);

        // Content
        JTextArea contentArea = new JTextArea(announcement.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setFont(Constants.NORMAL_FONT);
        contentArea.setForeground(Constants.TEXT_SECONDARY);
        contentArea.setBackground(new Color(255, 255, 255, 150));
        contentArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JLabel infoLabel = new JLabel(String.format("Posted by %s on %s",
                announcement.getPostedBy(),
                sdf.format(announcement.getPostedDate())));
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(Constants.TEXT_SECONDARY);

        footerPanel.add(infoLabel, BorderLayout.CENTER);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(contentArea, BorderLayout.CENTER);
        card.add(footerPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color glassColor = Constants.GLASS_BACKGROUND;
                g2d.setColor(Constants.SHADOW_COLOR);
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 15, 15);

                g2d.setColor(glassColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return card;
    }
}