module sk.kopr.projectmultithread {
    requires javafx.controls;
    requires javafx.fxml;

    opens sk.kopr.projectmultithread.client to javafx.fxml;
    exports sk.kopr.projectmultithread.client;
    opens sk.kopr.projectmultithread.server to javafx.fxml;
    exports sk.kopr.projectmultithread.server;
    opens sk.kopr.projectmultithread.client.gui to javafx.fxml;
    exports sk.kopr.projectmultithread.client.gui;
    opens sk.kopr.projectmultithread.server.gui to javafx.fxml;
    exports sk.kopr.projectmultithread.server.gui;
}
