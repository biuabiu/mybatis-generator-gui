package com.zzg.mybatis.generator.view;

import java.lang.ref.WeakReference;

import com.zzg.mybatis.generator.model.DatabaseConfig;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

/**
 * Created by Owen on 6/14/16.
 */
@SuppressWarnings(value = { "restriction" })
public class LeftDbTreeCell extends TreeCell<DatabaseConfig> {
	private WeakReference<TreeItem<DatabaseConfig>> treeItemRef;

	private InvalidationListener treeItemGraphicListener = observable -> {
		updateDisplay(getItem(), isEmpty());
	};

	private InvalidationListener treeItemListener = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
			TreeItem<DatabaseConfig> oldTreeItem = treeItemRef == null ? null : treeItemRef.get();
			if (oldTreeItem != null) {
				oldTreeItem.graphicProperty().removeListener(weakTreeItemGraphicListener);
			}

			TreeItem<DatabaseConfig> newTreeItem = getTreeItem();
			if (newTreeItem != null) {
				newTreeItem.graphicProperty().addListener(weakTreeItemGraphicListener);
				treeItemRef = new WeakReference<TreeItem<DatabaseConfig>>(newTreeItem);
			}
		}
	};

	private WeakInvalidationListener weakTreeItemGraphicListener = new WeakInvalidationListener(
			treeItemGraphicListener);

	private WeakInvalidationListener weakTreeItemListener = new WeakInvalidationListener(treeItemListener);

	public LeftDbTreeCell() {
		treeItemProperty().addListener(weakTreeItemListener);

		if (getTreeItem() != null) {
			getTreeItem().graphicProperty().addListener(weakTreeItemGraphicListener);
		}
	}

	void updateDisplay(DatabaseConfig item, boolean empty) {
		if (item == null || empty) {
			setText(null);
			setGraphic(null);
		} else {
			// update the graphic if one is set in the TreeItem
			TreeItem<DatabaseConfig> treeItem = getTreeItem();
			if (treeItem != null && treeItem.getGraphic() != null) {
				setText(item.toString());
				setGraphic(treeItem.getGraphic());
			} else {
				setText(item.getName());
				setGraphic(null);
			}
		}
	}

	@Override
	public void updateItem(DatabaseConfig item, boolean empty) {
		super.updateItem(item, empty);
		updateDisplay(item, empty);
	}
}
