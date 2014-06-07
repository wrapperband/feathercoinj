/**
 * Copyright 2011 Micheal Swiggs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.feathercoin.discovery;

import com.google.feathercoin.core.NetworkParameters;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * SeedPeers stores a pre-determined list of Feathercoin node addresses. These nodes are selected based on being
 * active on the network for a long period of time. The intention is to be a last resort way of finding a connection
 * to the network, in case IRC and DNS fail. The list comes from the Feathercoin C++ source code.
 */
public class SeedPeers implements PeerDiscovery {
    private NetworkParameters params;
    private int pnseedIndex;

    public SeedPeers(NetworkParameters params) {
        this.params = params;
    }

    /**
     * Acts as an iterator, returning the address of each node in the list sequentially.
     * Once all the list has been iterated, null will be returned for each subsequent query.
     *
     * @return InetSocketAddress - The address/port of the next node.
     * @throws PeerDiscoveryException
     */
    public InetSocketAddress getPeer() throws PeerDiscoveryException {
        try {
            return nextPeer();
        } catch (UnknownHostException e) {
            throw new PeerDiscoveryException(e);
        }
    }

    private InetSocketAddress nextPeer() throws UnknownHostException {
        if (pnseedIndex >= seedAddrs.length) return null;
        return new InetSocketAddress(convertAddress(seedAddrs[pnseedIndex++]),
                params.port);
    }

    /**
     * Returns an array containing all the Feathercoin nodes within the list.
     */
    public InetSocketAddress[] getPeers(long timeoutValue, TimeUnit timeoutUnit) throws PeerDiscoveryException {
        try {
            return allPeers();
        } catch (UnknownHostException e) {
            throw new PeerDiscoveryException(e);
        }
    }

    private InetSocketAddress[] allPeers() throws UnknownHostException {
        InetSocketAddress[] addresses = new InetSocketAddress[seedAddrs.length];
        for (int i = 0; i < seedAddrs.length; ++i) {
            addresses[i] = new InetSocketAddress(convertAddress(seedAddrs[i]), params.port);
        }
        return addresses;
    }

    private InetAddress convertAddress(int seed) throws UnknownHostException {
        byte[] v4addr = new byte[4];
        v4addr[0] = (byte) (0xFF & (seed));
        v4addr[1] = (byte) (0xFF & (seed >> 8));
        v4addr[2] = (byte) (0xFF & (seed >> 16));
        v4addr[3] = (byte) (0xFF & (seed >> 24));
        return InetAddress.getByAddress(v4addr);
    }

    public static int[] seedAddrs =
            {
                    0xc49c0ed8, 0x1df9d243, 0xd9b75a40, 0xdc525e46, 0x72c321b1, 0x4eedeeb2, 0x18271787, 0xce725232,
                    0x892aafc0, 0x979f6751, 0x2210f618, 0xfb529ed8, 0x66a74b3e, 0xef5d132e, 0x3b7a116c, 0x2fe45f55,
                    0x1df9d243, 0xe41f7c70, 0x8f4cd262, 0xb5c29d62, 0x80f1f3a2, 0x47dc614d, 0x6f458b4e, 0x908caa56,
                    0x553ef762, 0x5aec0852, 0x629a4f54, 0x6a10e13c, 0x4f41c547, 0x4476fd59, 0xcaedbc5a, 0x9f806dc1,
                    0x1df9d243, 0xe41f7c70, 0x8f4cd262, 0xb5c29d62, 0x80f1f3a2, 0x47dc614d, 0x6f458b4e, 0x908caa56,
                    0x553ef762, 0x5aec0852, 0x629a4f54, 0x6a10e13c, 0x4f41c547, 0x4476fd59, 0xcaedbc5a, 0x9f806dc1
            };
    
    public void shutdown() {
    }
}
