/*
 * Copyright (c) 2018, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.svm.core.posix.amd64;

import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

import com.oracle.svm.core.RegisterDumper;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.annotate.Uninterruptible;
import com.oracle.svm.core.graal.amd64.SubstrateAMD64RegisterConfig;
import com.oracle.svm.core.log.Log;
import com.oracle.svm.core.posix.UContextRegisterDumper;
import com.oracle.svm.core.posix.headers.Signal.GregEnum;
import com.oracle.svm.core.posix.headers.Signal.GregsPointer;
import com.oracle.svm.core.posix.headers.Signal.ucontext_t;
import com.oracle.svm.core.util.VMError;

import jdk.vm.ci.amd64.AMD64;

@Platforms({Platform.LINUX_AMD64.class})
@AutomaticFeature
class AMD64UContextRegisterDumperFeature implements Feature {
    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        VMError.guarantee(AMD64.r14.equals(SubstrateAMD64RegisterConfig.HEAP_BASE_REGISTER_CANDIDATE));
        VMError.guarantee(AMD64.r15.equals(SubstrateAMD64RegisterConfig.THREAD_REGISTER_CANDIDATE));
        ImageSingletons.add(RegisterDumper.class, new AMD64UContextRegisterDumper());
    }
}

class AMD64UContextRegisterDumper implements UContextRegisterDumper {
    @Override
    public void dumpRegisters(Log log, ucontext_t uContext) {
        GregsPointer gregs = uContext.uc_mcontext_gregs();
        log.string("RAX ").zhex(gregs.read(GregEnum.REG_RAX.getCValue())).newline();
        log.string("RBX ").zhex(gregs.read(GregEnum.REG_RBX.getCValue())).newline();
        log.string("RCX ").zhex(gregs.read(GregEnum.REG_RCX.getCValue())).newline();
        log.string("RDX ").zhex(gregs.read(GregEnum.REG_RDX.getCValue())).newline();
        log.string("RBP ").zhex(gregs.read(GregEnum.REG_RBP.getCValue())).newline();
        log.string("RSI ").zhex(gregs.read(GregEnum.REG_RSI.getCValue())).newline();
        log.string("RDI ").zhex(gregs.read(GregEnum.REG_RDI.getCValue())).newline();
        log.string("RSP ").zhex(gregs.read(GregEnum.REG_RSP.getCValue())).newline();
        log.string("R8  ").zhex(gregs.read(GregEnum.REG_R8.getCValue())).newline();
        log.string("R9  ").zhex(gregs.read(GregEnum.REG_R9.getCValue())).newline();
        log.string("R10 ").zhex(gregs.read(GregEnum.REG_R10.getCValue())).newline();
        log.string("R11 ").zhex(gregs.read(GregEnum.REG_R11.getCValue())).newline();
        log.string("R12 ").zhex(gregs.read(GregEnum.REG_R12.getCValue())).newline();
        log.string("R13 ").zhex(gregs.read(GregEnum.REG_R13.getCValue())).newline();
        log.string("R14 ").zhex(gregs.read(GregEnum.REG_R14.getCValue())).newline();
        log.string("R15 ").zhex(gregs.read(GregEnum.REG_R15.getCValue())).newline();
        log.string("EFL ").zhex(gregs.read(GregEnum.REG_EFL.getCValue())).newline();
        log.string("RIP ").zhex(gregs.read(GregEnum.REG_RIP.getCValue())).newline();
    }

    @Override
    @Uninterruptible(reason = "Called from uninterruptible code", mayBeInlined = true, calleeMustBe = false)
    public PointerBase getHeapBase(ucontext_t uContext) {
        GregsPointer gregs = uContext.uc_mcontext_gregs();
        return WordFactory.pointer(gregs.read(GregEnum.REG_R14.getCValue()));
    }

    @Override
    @Uninterruptible(reason = "Called from uninterruptible code", mayBeInlined = true, calleeMustBe = false)
    public PointerBase getThreadPointer(ucontext_t uContext) {
        GregsPointer gregs = uContext.uc_mcontext_gregs();
        return WordFactory.pointer(gregs.read(GregEnum.REG_R15.getCValue()));
    }

    @Override
    public PointerBase getSP(ucontext_t uContext) {
        GregsPointer gregs = uContext.uc_mcontext_gregs();
        return WordFactory.pointer(gregs.read(GregEnum.REG_RSP.getCValue()));
    }

    @Override
    public PointerBase getIP(ucontext_t uContext) {
        GregsPointer gregs = uContext.uc_mcontext_gregs();
        return WordFactory.pointer(gregs.read(GregEnum.REG_RIP.getCValue()));
    }
}
