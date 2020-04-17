//
// Created by Administrator on 2020/4/17.
//

#include "WlAudio.h"

WlAudio::WlAudio(WlPlaystatus *playstatus) {
    this->playstatus = playstatus;
    queue = new WlQueue(playstatus);
}

WlAudio::~WlAudio() {

}
