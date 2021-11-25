import {RiTimeLine} from 'react-icons/ri'
import {BsFillCloudArrowDownFill, BsFillCloudArrowUpFill} from 'react-icons/bs'
import moment from 'moment'

export default function NetworkSpeed({stat}) {
    let prev_stat = {
        measure: [],
        timestamp: Date.now()
    }
    const icons = {
        DOWNLOAD: <BsFillCloudArrowDownFill />,
        UPLOAD: <BsFillCloudArrowUpFill />
    }
    prev_stat = stat ? stat : prev_stat
    return (
        <div className='infra-name-ctn' id='cpu'>
            <h4>Network Speed</h4>
            {prev_stat.measure.map((stat, index) => <div className='measure_stat' key={index}>{icons[stat.networkMode]}<p><b>{stat.networkMode}</b>: {stat.speed}</p></div>)}
            <div className='measure_stat' id='timestamp'>
                <RiTimeLine />
                <p>updated {moment(prev_stat.timestamp).fromNow()}</p>
            </div>
        </div>
    )
}