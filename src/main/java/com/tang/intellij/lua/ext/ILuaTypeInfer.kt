/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tang.intellij.lua.ext

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.progress.ProgressManager
import com.tang.intellij.lua.index.IndexManager
import com.tang.intellij.lua.psi.LuaTypeGuessable
import com.tang.intellij.lua.search.SearchContext
import com.tang.intellij.lua.ty.ITy
import com.tang.intellij.lua.ty.Ty

interface ILuaTypeInfer {
    companion object {
        private val EP_NAME = ExtensionPointName.create<ILuaTypeInfer>("com.tang.intellij.lua.luaTypeInfer")


        fun infer(target: LuaTypeGuessable, context: SearchContext): ITy {
            val indexManager = IndexManager.getInstance(context.project)
            val lazyTy = indexManager.tryInfer(target, context)
            return lazyTy ?: inferImpl(target, context)
        }

        private fun inferImpl(target: LuaTypeGuessable, context: SearchContext): ITy {
            for (typeInfer in EP_NAME.extensions) {
                ProgressManager.checkCanceled()
                val iTy = typeInfer.inferType(target, context)
                if (!Ty.isInvalid(iTy))
                    return iTy
            }
            return Ty.UNKNOWN
        }
    }

    fun inferType(target: LuaTypeGuessable, context: SearchContext): ITy
}