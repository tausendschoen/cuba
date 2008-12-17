/*
 * Copyright (c) 2008 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: Konstantin Krivopustov
 * Created: 16.12.2008 19:32:37
 *
 * $Id$
 */
package com.haulmont.cuba.core.impl.listener;

import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;

public enum EntityListenerType
{
    BEFORE_UPDATE(BeforeUpdateEntityListener.class),
    BEFORE_DELETE(BeforeDeleteEntityListener.class);
    
    private final Class listenerInterface;

    private EntityListenerType(Class listenerInterface) {
        this.listenerInterface = listenerInterface;
    }

    public Class getListenerInterface() {
        return listenerInterface;
    }
    
}
