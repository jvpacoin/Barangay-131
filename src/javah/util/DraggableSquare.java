package javah.util;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;

/**
 * The class to create a draggable square for image cropping.
 */
public class DraggableSquare extends Rectangle {

    private double handleRadius = 10;

    private Circle mResizeHandleNW, mResizeHandleSE, mMoveHandle;

    public DraggableSquare(int x, int y, int side, int parentWidth, int parentHeight) {
        super(x, y, side, side);

        this.setFill(Color.TRANSPARENT);
        this.setStroke(Color.WHITE);

        // top left resize handle:
        mResizeHandleNW = new Circle(handleRadius, Color.GOLD);
        // bind to top left corner of Rectangle:
        mResizeHandleNW.centerXProperty().bind(this.xProperty());
        mResizeHandleNW.centerYProperty().bind(this.yProperty());

        // bottom right resize handle:
        mResizeHandleSE = new Circle(handleRadius, Color.GOLD);
        // bind to bottom right corner of Rectangle:
        mResizeHandleSE.centerXProperty().bind(this.xProperty().add(this.widthProperty()));
        mResizeHandleSE.centerYProperty().bind(this.yProperty().add(this.heightProperty()));

        // move handle:
        mMoveHandle = new Circle(handleRadius, Color.GOLD);
        // bind to bottom center of Rectangle:
        mMoveHandle.centerXProperty().bind(this.xProperty().add(this.widthProperty().divide(2)));
        mMoveHandle.centerYProperty().bind(this.yProperty().add(this.heightProperty()));

        // force circles to live in same parent as rectangle:
        this.parentProperty().addListener((obs, oldParent, newParent) -> {
            for (Circle c : Arrays.asList(mResizeHandleNW, mResizeHandleSE, mMoveHandle)) {
                Pane currentParent = (Pane)c.getParent();
                if (currentParent != null) {
                    currentParent.getChildren().remove(c);
                }
                ((Pane)newParent).getChildren().add(c);
            }
        });

        mResizeHandleNW.setOnMouseDragged(event -> {
            double deltaX = event.getX() - this.getX();
            double newX = event.getX();
            double newY = this.getY() + deltaX;

            // Drag HandleNW if the handle is still inside the pane and the HandleNW is not touching HandleSE.
            if (newX > handleRadius && newY > handleRadius && newX <= this.getX() + this.getWidth() - handleRadius) {
                this.setX(newX);
                this.setY(newY);

                this.setWidth(this.getWidth() - deltaX);
                this.setHeight(this.getWidth());
            }
        });

        mResizeHandleSE.setOnMouseDragged(event -> {
            double deltaX = event.getX() - (this.getX() + this.getWidth());
            double newX = this.getX() + this.getWidth() + deltaX;
            double newY = this.getY() + this.getHeight() + deltaX;

            // Drag HandleSE if the handle is still inside the pane and the HandleSE is not touching HandleNW.
            if (newX > this.getX() + handleRadius && newX < parentWidth - handleRadius && newY < parentHeight - handleRadius) {
                this.setWidth(this.getWidth() + deltaX);
                this.setHeight(this.getWidth());
            }
        });

        mMoveHandle.setOnMouseDragged(event -> {
            double deltaX = event.getX() - (this.getX() + this.getWidth() / 2);
            double deltaY = event.getY() - (this.getHeight() + this.getY());

            // New HandleNW Coordinates.
            double newX1 = this.getX() + deltaX;
            double newY1 = this.getY() + deltaY;

            // New HandleSE Coordinates.
            double newX2 = newX1 + this.getWidth();
            double newY2 = newY1 + this.getHeight();

            // Drag the square along the x-axis if it does not touch either of the edges.
            if (newX1 > handleRadius && newX2 < parentWidth - handleRadius)
                this.setX(newX1);

            // Drag the square along the y-axis if it does not touch either of the edges.
            if (newY1 > handleRadius && newY2 < parentHeight - handleRadius)
                this.setY(newY1);
        });
    }

    /**
     * Manage visibility of the square and its handles.
     * @param isVisible
     */
    public void setmVisible(boolean isVisible) {
        this.setVisible(isVisible);
        mResizeHandleNW.setVisible(isVisible);
        mResizeHandleSE.setVisible(isVisible);
        mMoveHandle.setVisible(isVisible);
    }

    /**
     * Set the square and its handles to the front.
     */
    public void setmToFront() {
        this.toFront();
        mResizeHandleNW.toFront();
        mResizeHandleSE.toFront();
        mMoveHandle.toFront();
    }
}
