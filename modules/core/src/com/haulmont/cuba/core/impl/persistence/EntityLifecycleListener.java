/*
 * Copyright (c) 2008 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: Konstantin Krivopustov
 * Created: 11.11.2008 18:14:37
 *
 * $Id$
 */
package com.haulmont.cuba.core.impl.persistence;

import com.haulmont.cuba.core.SecurityProvider;
import com.haulmont.cuba.core.PersistenceProvider;
import com.haulmont.cuba.core.impl.listener.EntityListenerManager;
import com.haulmont.cuba.core.impl.listener.EntityListenerType;
import com.haulmont.cuba.core.entity.BaseEntity;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.DeleteDeferred;
import com.haulmont.cuba.core.global.TimeProvider;
import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.event.AbstractLifecycleListener;
import org.apache.openjpa.event.LifecycleEvent;

import java.util.Date;
import java.util.Arrays;

public class EntityLifecycleListener extends AbstractLifecycleListener
{
    public void beforePersist(LifecycleEvent event) {
        if ((event.getSource() instanceof BaseEntity)) {
            __beforePersist((BaseEntity) event.getSource());
        }
    }

    public void beforeStore(LifecycleEvent event) {
        PersistenceCapable pc = (PersistenceCapable) event.getSource();
        if (!pc.pcIsNew() && (pc instanceof Updatable)) {
            __beforeUpdate((Updatable) event.getSource());
            if ((pc instanceof DeleteDeferred) && justDeleted((DeleteDeferred) pc)) {
                EntityListenerManager.getInstance().fireListener(
                        ((BaseEntity) event.getSource()), EntityListenerType.BEFORE_DELETE);
            }
            else {
                EntityListenerManager.getInstance().fireListener(
                        ((BaseEntity) event.getSource()), EntityListenerType.BEFORE_UPDATE);
            }
        }
    }

    private boolean justDeleted(DeleteDeferred dd) {
        if (!dd.isDeleted()) {
            return false;
        }
        else {
            String[] fields = PersistenceProvider.getDirtyFields((BaseEntity) dd);
            Arrays.sort(fields);
            return Arrays.binarySearch(fields, "deleteTs") >= 0;
        }
    }

    public void afterStore(LifecycleEvent event) {
        PersistenceCapable pc = (PersistenceCapable) event.getSource();
        if (!pc.pcIsNew() && (event.getSource() instanceof Updatable)) {
//            System.out.println("afterStore: " + pc);
        }
    }

    private void __beforePersist(BaseEntity entity) {
        entity.setCreatedBy(SecurityProvider.currentUserLogin());
        Date ts = TimeProvider.currentTimestamp();
        entity.setCreateTs(ts);

        if (entity instanceof Updatable) {
            ((Updatable) entity).setUpdateTs(TimeProvider.currentTimestamp());
        }
    }

    private void __beforeUpdate(Updatable entity) {
        String user = SecurityProvider.currentUserLogin();
        entity.setUpdatedBy(user);
        entity.setUpdateTs(TimeProvider.currentTimestamp());
    }
}
