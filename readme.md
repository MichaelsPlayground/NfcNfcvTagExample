# NFC NFCV tag example

This app reads and writes data to a tag that uses the **NFCV technology**. There seems to be some special  
uses cases where those tags are in use.

I am working with a **skiing pass** that was issued by (Austrian) "**Oetztal GURGL**" skiing area and 
enables the holder to access the transportation units like ski lifts and busses.

What I noticed when working with this tag is the **extended range** when the tag is detected by my Android device. 
The reason may be simple - those tags are located in pockets in the arm of the ski jacket and the tag should be 
discovered by the reader without pulling out the tag (remember - you are wearing thick gloves) out of the pocket. 
So the ski driver just brings his arm near to the NFC reader and he is done.

The bad news are - I could not find any matching datasheet for the tag (maybe it is an EM4233 tag - in a forum I found a 
note that it is NOT the EM4233SLIC tag): 
https://www.emmicroelectronic.com/sites/default/files/products/datasheets/em4233slic_ds.pdf

Try to find them on manufacturer's website: https://www.emmicroelectronic.com/product?title=em4&term_node_tid_depth=All

Using NXP's TagInfo I could read these information's about the tag:

```plaintext
IC manufacturer: EM Microelectronic-Marin SA
IC type:         EM4x3x
Application information: SKIDATA keycard
Key number: xx-16147133535046684872-x
Area: Obergurgl Hochgurgl
(Probably)Valid till: 11/05/2027 (AB:28)

Memory information: 208 bytes in 52 blocks, with 4 bytes per block

IC information: Supported read commands, Single Block Read, Multiple Block Read, Get Multiple Block Security Status, Get System Information
AFI supported, DSFID supported, IC reference value: 0x02, Customer ID: 0x066

Technologies supported: ISO/IEC 15693-3 compatible, ISO/IEC 15693-2 compatible
Android technology information: Tech [android.nfc.tech.NfcV, android.nfc.tech.NdefFormatable]
Detailed protocol information:
ID:    E0:16:24:66:21:41:50:C8
AFI:   0x00
DSFID: 0x02
```

Using **NFC Taginfo** there are 52 blocks of (each) 4 byte so in total the tag has a storage capacity of 208 bytes. The blocks are not blocked and writable.

As the tag is fully readable but write protected I do not provide any module that tries to write to the tag, sorry.


Icons: https://www.freeiconspng.com/images/nfc-icon

Nfc Simple PNG Transparent Background: https://www.freeiconspng.com/img/20581

<a href="https://www.freeiconspng.com/img/20581">Nfc Png Simple</a>

Minimum SDK is 21 (Android 5)



