/*
 * Copyright (c) 2020 Squirrel Chat, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package chat.squirrel.database.entities.messages.impl;

import chat.squirrel.database.entities.messages.AbstractMessage;
import chat.squirrel.database.entities.messages.IMessageEmbed;
import chat.squirrel.database.entities.messages.IMessageGadget;
import chat.squirrel.database.entities.messages.ITextMessage;

import java.util.Collection;

public class TextMessageImpl extends AbstractMessage implements ITextMessage {
    private String contents;
    private Collection<IMessageEmbed> embeds;
    private Collection<IMessageGadget> gadgets;

    @Override
    public String getContents() {
        return contents;
    }

    @Override
    public void setContents(final String contents) {
        this.contents = contents;
    }

    @Override
    public Collection<IMessageEmbed> getEmbeds() {
        return embeds;
    }

    @Override
    public void setEmbeds(final Collection<IMessageEmbed> embeds) {
        this.embeds = embeds;
    }

    @Override
    public Collection<IMessageGadget> getGadgets() {
        return gadgets;
    }

    @Override
    public void setGadgets(final Collection<IMessageGadget> gadgets) {
        this.gadgets = gadgets;
    }
}
