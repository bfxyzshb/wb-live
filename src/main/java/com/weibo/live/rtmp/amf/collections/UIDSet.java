/*
  GRANITE DATA SERVICES
  Copyright (C) 2007 ADEQUATE SYSTEMS SARL

  This file is part of Granite Data Services.

  Granite Data Services is free software; you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 3 of the License, or (at your
  option) any later version.
 
  Granite Data Services is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
  for more details.
 
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, see <http://www.gnu.org/licenses/>.
*/

package com.weibo.live.rtmp.amf.collections;

import java.util.Collection;

import com.weibo.live.rtmp.amf.messaging.io.ArrayCollection;

/**
 * @author Franck WOLFF
 */
public class UIDSet extends ArrayCollection {

	public UIDSet() {
	}

	public UIDSet(int capacity) {
		super(capacity);
	}

	public UIDSet(Collection<?> col) {
		super(col);
	}

	public UIDSet(Object[] array) {
		super(array);
	}
}
