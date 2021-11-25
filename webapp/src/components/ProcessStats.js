import {RiTimeLine} from 'react-icons/ri'
import {BiTask} from 'react-icons/bi'
import moment from 'moment'

export default function ProcessStats({stat}) {
    let prev_stat = {
        measure: 'fetching details',
        timestamp: Date.now()
    }
    prev_stat = stat ? stat : prev_stat
    return (
        <div className='infra-name-ctn' id="process">
            <h4>Processes Stats</h4>
            <div className='measure_stat'>
                <BiTask />
                <p>{prev_stat.measure}</p>
            </div>
            <div className='measure_stat' id='timestamp'>
                <RiTimeLine />
                <p>updated {moment(prev_stat.timestamp).fromNow()}</p>
            </div>
        </div>
    )
}