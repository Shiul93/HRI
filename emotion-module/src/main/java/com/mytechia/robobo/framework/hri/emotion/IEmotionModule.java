/*******************************************************************************
 *
 *   Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2016 Gervasio Varela <gervasio.varela@mytechia.com>
 *
 *   This file is part of Robobo Emotion Module.
 *
 *   Robobo Emotion Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo Emotion Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo Emotion Module.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.mytechia.robobo.framework.hri.emotion;

import com.mytechia.robobo.framework.IModule;

/** Default interface of the a Robobo emotion module.
 */
public interface IEmotionModule extends IModule {


    public void setCurrentEmotion(Emotion emotion);


    public Emotion getCurrentEmotion();


    public void setTemporalEmotion(Emotion temporalEmotion, long duration, Emotion nextEmotion);


    public void subscribe(IEmotionListener listener);

    public void ubsubscribe(IEmotionListener listener);

    public void subscribeTouchListener(ITouchEventListener listener);

    public void unsubscribeTouchListener(ITouchEventListener listener);


}
